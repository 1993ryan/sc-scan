package nuaa.dp.hole.dal.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

//@JsonIgnoreProperties(value = {"endPage", "pageCount", "sort", "startIndex", "startPage", "totalPage"})
public class PageInfo implements Serializable {
	private static final long serialVersionUID = -8129017250639004158L;
	
	public static final int PAGE_SIZE_DEFAULT = 10;
	public static final int PAGES_PER_TIME_DEFAULT = 5;

	private int rowCount = 0;
	private int pageIndex = 1;
	private int pageSize = PAGE_SIZE_DEFAULT;

	private String sort = "";

	/** asc, desc */
	private String order = "asc";

	public PageInfo() {
	}

	public PageInfo(int pageSize, int rowCount, int pageIndex) {
		super();
		this.pageSize = pageSize;
		this.rowCount = rowCount;
		this.pageIndex = pageIndex;
	}

	public final int getPageSize() {
		return pageSize;
	}

	public final int getPageIndex() {
		return pageIndex;
	}

	public final int getRowCount() {
		return rowCount;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = (0 >= pageIndex) ? 1 : pageIndex;
	}

	@JsonIgnore
	public int getStartIndex() {
		return (this.getPageIndex() - 1) * this.getPageSize();
	}

	@JsonIgnore
	public final int getPageCount() {
		return (rowCount + pageSize - 1) / pageSize;
	}

	@JsonIgnore
	public int getTotalPage() {
		return (this.getRowCount() - 1) / this.getPageSize() + 1;
	}

	@JsonIgnore
	public int getStartPage() {
		int startPage = 0;
		if (this.pageIndex >= this.getTotalPage() - PAGES_PER_TIME_DEFAULT / 2) {
			startPage = this.getTotalPage() - PAGES_PER_TIME_DEFAULT + 1;
		} else {
			startPage = this.pageIndex - PAGES_PER_TIME_DEFAULT / 2;
		}
		if (startPage < 1) {
			startPage = 1;
		}
		return startPage;
	}

	@JsonIgnore
	public int getEndPage() {
		int endPage = this.getStartPage() + PAGES_PER_TIME_DEFAULT - 1;
		if (endPage > this.getTotalPage()) {
			endPage = this.getTotalPage();
		}
		return endPage;
	}

	@JsonIgnore
	public String getSort() {
		return sort;
	}

	@JsonIgnore
	public String getSortBy(){
		if(StringUtils.isEmpty(sort)){
			return StringUtils.EMPTY;
		}
		
		StringBuilder s = new StringBuilder(sort.length());
		char[] arr = sort.toCharArray();
		for(int i=0;i<arr.length;i++){
			if(arr[i]>='A' && arr[i]<='Z'){
				s.append('_').append((char)(arr[i]+'a'-'A'));
			}else{
				s.append(arr[i]);
			}
		}
		return s.toString();
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	@JsonIgnore
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("pageIndex:").append(pageIndex).append(",");
		str.append("pageSize:").append(pageSize).append(",");
		str.append("rowCount:").append(rowCount);
		return str.toString();
	}
}
