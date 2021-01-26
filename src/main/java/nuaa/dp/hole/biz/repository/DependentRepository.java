package nuaa.dp.hole.biz.repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nuaa.dp.hole.biz.service.ITaskQueueService;
import nuaa.dp.hole.biz.service.ProjectDependService;
import nuaa.dp.hole.cons.OrderStatus;
import nuaa.dp.hole.dal.common.PageInfo;
import nuaa.dp.hole.dal.common.PageResult;
import nuaa.dp.hole.dal.common.Result;
import nuaa.dp.hole.dal.model.ProjectDependent;
import nuaa.dp.hole.dal.model.ProjectInfo;
import nuaa.dp.hole.dto.ProjectDependentQueryDto;
import nuaa.dp.hole.task.base.TaskTopic;
import nuaa.dp.hole.task.bean.TaskCreateDto;
import nuaa.dp.hole.utils.HttpResult;
import nuaa.dp.hole.utils.HttpUtils;
import nuaa.dp.hole.utils.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class DependentRepository {

    @Autowired
    private ProjectDependService projectDependService;

    @Autowired
    private ITaskQueueService taskQueueService;

    public Result<Integer> findDependents(ProjectInfo projectInfo) throws Exception {
        if(!projectInfo.getRemoteUrl().contains("github.com")) {
            return Result.failed("当前工程不是Github项目");
        }

        new Thread(()-> {
            try {
                doFindDependents(projectInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return Result.success(1);
    }

    private void doFindDependents(ProjectInfo projectInfo) {
        Result<List<String>> result = this.queryAllDependents(projectInfo);
        if(!result.isSuccess()) {
            log.error("get project dependents fail: {}", result.getMessage());
        }
    }

    public Result<Integer> fetchPomFiles(ProjectInfo projectInfo) throws Exception {
        new Thread(()-> {
            try {
                doFetchPomFiles(projectInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return Result.success(1);
    }

    private void doFetchPomFiles(ProjectInfo projectInfo) {
        ProjectDependentQueryDto queryDto = new ProjectDependentQueryDto();
        queryDto.setProjectId(projectInfo.getId());

        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageSize(Integer.MAX_VALUE);
        PageResult<ProjectDependent> pageResult = this.projectDependService.queryForList(queryDto, pageInfo);
        if(pageResult.getPageInfo().getRowCount() == 0) {
            return;
        }

        List<ProjectDependent> dataList = pageResult.getData();
        for(ProjectDependent item: dataList) {
            if(OrderStatus.SUCCESS.name().equals(item.getOrderStatus())) {
                continue;
            }
            TaskCreateDto tcd = new TaskCreateDto(TaskTopic.FETCH_POM_CONTENT);
            tcd.setBizId(item.getId());
            this.taskQueueService.insert(tcd);
        }
    }

    public Result<List<String>> queryAllDependents(ProjectInfo projectInfo) {
        String baseUrl = projectInfo.getRemoteUrl().replaceAll("\\.git", "/network/dependents")
                .replaceAll("git@github.com:", "https://github.com/")
                .replaceAll("git:", "https:");
        List<String> dependents = new ArrayList<>();
        try {
            this.findMoreDependents(projectInfo, baseUrl, dependents, true);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("query dependents fail: {}", e);
            return Result.failed("查询依赖本项目列表出错：" + e.getMessage());
        }
        return Result.success(dependents);
    }

    private void findMoreDependents(ProjectInfo projectInfo, String baseUrl, List<String> dependents, boolean findMorePackage) throws Exception {
        String strResponse = HttpUtils.doGet(baseUrl);
        log.info("RESPONSE<<<==={}", strResponse);

        Document doc = Jsoup.parse(strResponse);
        Elements packageElements = doc.select(".select-menu-list A");
        if(findMorePackage && 0 < packageElements.size()) {
            for(int index = 0; index < packageElements.size(); index++) {
                Element item = packageElements.get(index);
                String strHref = item.attr("href");
                if(!strHref.contains("github.com")) {
                    strHref = "https://github.com/" + strHref;
                }

                try {
                    String pageResp = HttpUtils.doGet(strHref);

                    Document pageDoc = Jsoup.parse(pageResp);
                    doFetchMoreDependents(projectInfo, pageDoc, dependents);
                } catch(Exception e) {
                    log.error("findMoreDependents url: {}", strHref, e);
                }
            }
        } else {
            doFetchMoreDependents(projectInfo, doc, dependents);
        }
    }

    private void doFetchMoreDependents(ProjectInfo projectInfo, Document doc, List<String> dependents) throws Exception {
        Elements elements = doc.select("#dependents .Box .Box-row span.text-gray-light");
        String artifactId = null;

        Elements packageElements = doc.select("#dependents .mb-4 strong");
        if(Objects.nonNull(packageElements) && packageElements.size() > 0) {
            String packageName = packageElements.get(0).text();
            if(packageName.contains(":")) {
                artifactId = packageName.substring(packageName.indexOf(":") + 1);
            }
        }

        for(int index = 0; index < elements.size(); index++) {
            Element item = elements.get(index);
            Element hrefItem = item.selectFirst("a.text-bold");
            if(Objects.isNull(hrefItem)) {
                continue;
            }

            String simpleName = item.text().replaceAll(" ", "");
            dependents.add(simpleName);

            this.projectDependService.insertDependent(projectInfo.getId(), artifactId, simpleName);
        }

        Elements paginateButtons = doc.select(".paginate-container a");
        for (Element item : paginateButtons) {
            String strText = item.text();
            if(StringUtils.isNotBlank(strText) && strText.contains("Next")) {
                Element hrefItem = item.selectFirst("A");
                if(Objects.nonNull(hrefItem) && StringUtils.isNotBlank(hrefItem.attr("href"))) {
                    String strHref = hrefItem.attr("href");
                    if(!strHref.contains("github.com")) {
                        strHref = "https://github.com/" + strHref;
                    }
                    this.findMoreDependents(projectInfo, strHref, dependents, false);
                }
            }
        }
    }

    public boolean fetchPomFileContent(ProjectDependent projectDependent) throws Exception {
        boolean result = true;
        if(Objects.nonNull(projectDependent.getStatusCode()) && 200 == projectDependent.getStatusCode()) {
            return true;
        }

        ProjectDependent exists = this.projectDependService.checkIfTaskItemExists(projectDependent);
        if(Objects.isNull(exists)) {
            String rootPomUrl = String.format("https://api.github.com/repos/%s/contents/pom.xml", projectDependent.getSimpleName());
            HttpGet getMethod = new HttpGet(rootPomUrl);
            getMethod.addHeader("User-Agent", HttpUtils.USER_AGENT_DEFAULT);
            getMethod.addHeader("Accept", "application/vnd.github.v3+json");
            getMethod.addHeader("Authorization", "token c1f9c2a16c24e8dde4b36328c52c6b2dc24c49b0");
            HttpResult httpResult = HttpUtils.doHttpRequest(getMethod);
            JSONObject json = JSON.parseObject(new String(httpResult.getData()));
            if(Objects.nonNull(json) && StringUtils.isNotBlank(json.getString("content"))) {
                String strContent = new String(Base64.decodeBase64(json.getString("content")));
                projectDependent.setPomContent(strContent);
            }
            projectDependent.setStatusCode(httpResult.getStatusCode());

            result = (403 != httpResult.getStatusCode());
        } else {
            projectDependent.setOrderStatus(OrderStatus.SUCCESS.name());
            projectDependent.setStatusCode(exists.getStatusCode());
            projectDependent.setPomContent(exists.getPomContent());
        }
        this.projectDependService.saveDependent(projectDependent);

        return result;
    }
}
