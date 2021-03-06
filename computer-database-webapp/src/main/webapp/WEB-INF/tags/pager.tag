<%@ tag body-content="empty" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cst" tagdir="/WEB-INF/tags" %>

<c:set var="pagerRange" scope="page" value="${5}"/>
<c:set var="range" scope="page" value="${ pageDTO.maxPageNumber < pagerRange ? pageDTO.maxPageNumber : pagerRange }"/>

<c:set var="half" scope="page" value="${ range / 2 }"/>
<%-- without the following line, 6 list number are displayed instead of five --%>
<%-- indeed, the numbers in JSPs are Double by default, so %1 removes the digits after the comma --%>
<c:set var="half" scope="page" value="${ half - (half % 1) }"/>

<c:set var="start" scope="page" value="${ pageDTO.currentPageNumber - half }"/>
<c:set var="stop" scope="page" value="${ pageDTO.currentPageNumber + half }"/>

<c:if test="${start <= 0}" >
    <c:set var="start" scope="page" value="${0}"/>
    <c:set var="stop" scope="page" value="${(pagerRange < pageDTO.maxPageNumber) ? pagerRange : pageDTO.maxPageNumber}"/>
</c:if>
<c:if test="${stop >= pageDTO.maxPageNumber}" >
    <c:set var="start" scope="page" value="${pageDTO.maxPageNumber-pagerRange <= 0 ? 0 : pageDTO.maxPageNumber-pagerRange}"/>
    <c:set var="stop" scope="page" value="${pageDTO.maxPageNumber}"/>
</c:if>

<ul class="pagination">
    <li>
        <a href='<cst:links target="self" pageNb="0" search="${searchField}"
        field="${orderBy}" ascending="${isAscending}"/>' aria-label="Previous">
            <span aria-hidden="true"><spring:message code="pager.first" /></span>
        </a>
    </li>
    <li>
        <a href='<cst:links target="self"
        pageNb="${ pageDTO.currentPageNumber-1 < 0 ? 0 : pageDTO.currentPageNumber-1 }"
        search="${searchField}" field="${orderBy}" ascending="${isAscending}"/>'
           aria-label="Previous">
            <span aria-hidden="true">&laquo;</span>
        </a>
    </li>

    <%-- the current list is not displayed as a link--%>
    <c:forEach var="i" begin="${ start }" end="${ stop }" step="1">
        <c:choose>
            <c:when test="${ i == pageDTO.currentPageNumber }">
                <li><a class="disabled" style="color: black"><c:out value="${ i + 1 } "/></a></li>
            </c:when>
            <c:otherwise>
                <li><a href='<cst:links target="self" pageNb="${ i }" search="${searchField}" field="${orderBy}" ascending="${isAscending}"/>'><c:out value="${ i + 1 }"/></a></li>
            </c:otherwise>
        </c:choose>
    </c:forEach>

    <li>
        <a href='<cst:links target="self"
        pageNb="${ pageDTO.currentPageNumber+1 > pageDTO.maxPageNumber ? pageDTO.maxPageNumber : pageDTO.currentPageNumber+1 }"
        field="${orderBy}" ascending="${isAscending}" search="${searchField}"/>' aria-label="Next">
            <span aria-hidden="true">&raquo;</span>
        </a>
    </li>
    <li>
        <a href='<cst:links target="self" pageNb="${pageDTO.maxPageNumber}" search="${searchField}" field="${orderBy}" ascending="${isAscending}"/>'
           aria-label="Previous">
            <span aria-hidden="true"><spring:message code="pager.last" /></span>
        </a>
    </li>
</ul>