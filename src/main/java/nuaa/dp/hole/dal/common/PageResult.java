package nuaa.dp.hole.dal.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.Serializable;
import java.util.List;

public class PageResult<T> implements Serializable {
	private static final long serialVersionUID = 7419784354053517349L;

	private final List<T> data;
	private final PageInfo pageInfo;
	private boolean hasMore = false;

	private int code;
	private String message;

	public PageResult(List<T> result, PageInfo pageInfo) {
		this.data = result;
		this.code = ErrorCode.SUCCESS.code;
		this.pageInfo = pageInfo;
	}

	public static <T> PageResult<T> newErr(int errCode, String errMsg) {
		return new PageResult<T>(errCode, errMsg);
	}
	public static <T> PageResult<T> newErr(ErrorCode errorCode) {
		return new PageResult<T>(errorCode.code, errorCode.desc);
	}

	private PageResult(int errCode, String errMsg) {
		this.data = null;
		this.code = errCode;
		this.message = errMsg;
		this.pageInfo = null;
	}

	public List<T> getData() {
		return data;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	@JsonIgnore
	public boolean isSuccess() {
		return ErrorCode.SUCCESS.code == this.code;
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this);
	}
}
