package qe.entity.result;

import java.util.LinkedList;

/**
 * It represents one query failure - one compare error file.
 *
 * @author felias
 */
public class QueryFailure {
	private String actualResult;
	private String expectedResult;
	private String query;
	private String fileName;
	private String queryName;
	private LinkedList<String> compareErrors;

	public String getActualResult() {
		return actualResult;
	}

	public void setActualResult(String actualResult) {
		this.actualResult = actualResult;
	}

	public String getExpectedResult() {
		return expectedResult;
	}

	public void setExpectedResult(String expectedResult) {
		this.expectedResult = expectedResult;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public LinkedList<String> getCompareErrors() {
		return compareErrors;
	}

	public void addCompareError(String error) {
		if (compareErrors == null) {
			compareErrors = new LinkedList<String>();
		}
		compareErrors.add(error);
	}

	public void setCompareErrors(LinkedList<String> compareErrors) {
		this.compareErrors = compareErrors;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

}
