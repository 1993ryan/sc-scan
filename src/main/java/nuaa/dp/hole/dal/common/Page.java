package nuaa.dp.hole.dal.common;

/**
 * @Copyright MiXuan
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2019-02-20 23:36
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2019-02-20      Dapeng Yan          v1.0.0
 */
public class Page {
    private int pageIndex;
    private int pageSize = PageInfo.PAGE_SIZE_DEFAULT;
    private String orderByClause;
    private String order = "desc";

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public PageInfo toPageInfo() {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageIndex(this.pageIndex);
        pageInfo.setPageSize(this.pageSize);
        pageInfo.setSort(this.orderByClause);
        pageInfo.setOrder(this.order);
        return pageInfo;
    }
}