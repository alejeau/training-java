<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="cst" tagdir="/WEB-INF/tags" %>

<!DOCTYPE html>
<html>
<head>
    <title>Computer Database</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="utf-8">
    <!-- Bootstrap -->
    <link href="<c:url value="/static"/>/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="<c:url value="/static"/>/css/font-awesome.css" rel="stylesheet" media="screen">
    <link href="<c:url value="/static"/>/css/main.css" rel="stylesheet" media="screen">
</head>
<body>
<header class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <a class="navbar-brand" href="<cst:links target="reset" />"> Application - Computer Database </a>
    </div>
</header>

<section id="main">
    <div class="container">
        <h1 id="homeTitle">
            ${pageDTO.numberOfEntries} Computers found
        </h1>
        <div id="actions" class="form-horizontal">
            <div class="pull-left">
                <form id="searchForm" action="#" method="GET" class="form-inline">

                    <input type="search" id="searchbox" name="search" class="form-control" placeholder="Search name"/>
                    <input type="submit" id="searchsubmit" value="Filter by name"
                           class="btn btn-primary"/>
                </form>
            </div>
            <div class="pull-right">
                <a class="btn btn-success" id="addComputer" href="<cst:links target="add" />">Add Computer</a>
                <a class="btn btn-default" id="editComputer" href="#" onclick="$.fn.toggleEditMode();">Delete</a>
            </div>
        </div>
    </div>

    <form id="deleteForm" action="#" method="POST">
        <input type="hidden" name="selection" value="">
    </form>

    <div class="container" style="margin-top: 10px;">
        <table class="table table-striped table-bordered">
            <thead>
            <tr>
                <!-- Variable declarations for passing labels as parameters -->
                <!-- Table header for Computer Name -->

                <th class="editMode" style="width: 60px; height: 22px;">
                    <input type="checkbox" id="selectall"/>
                    <span style="vertical-align: top;">
                                     -  <a href="#" id="deleteSelected" onclick="$.fn.deleteSelected();">
                                            <i class="fa fa-trash-o fa-lg"></i>
                                        </a>
                        </span>
                </th>
                <th>Computer name</th>
                <th>Introduced date</th>
                <!-- Table header for Discontinued Date -->
                <th>Discontinued date</th>
                <!-- Table header for Company -->
                <th>Company</th>
            </tr>
            </thead>
            <!-- Browse attribute computers -->
            <tbody id="results">
            <c:forEach items="${ pageDTO.content }" var="computer">
                <tr>
                    <td class="editMode"><input type="checkbox" name="cb" class="cb"
                                                value="<c:out value="${ computer.id }" />"></td>
                    <td>
                        <a href="<cst:links target="edit" />&computerId=${ computer.id }">
                            <c:out value="${ computer.name }"/>
                        </a>
                    </td>
                    <td><c:out value="${ computer.introduced }"/></td>
                    <td><c:out value="${ computer.discontinued }"/></td>
                    <td><c:out value="${ computer.companyName }"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<footer class="navbar-fixed-bottom">
    <div class="container text-center">
        <cst:pager/>
        <cst:displayBy/>
    </div>
</footer>

<script src="<c:url value="/static"/>/js/jquery.min.js"></script>
<script src="<c:url value="/static"/>/js/bootstrap.min.js"></script>
<script src="<c:url value="/static"/>/js/dashboard.js"></script>

</body>
</html>