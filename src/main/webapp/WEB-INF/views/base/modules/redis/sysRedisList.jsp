<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%@ include file="/WEB-INF/views/include/head.jsp" %>
	<title>缓存管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#searchForm").validate({
				submitHandler: function(form){
					loading('正在查询，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }

        /**
         * 根据秒数获取时间串
         *
         * @param second 秒数
         * @return (返回格式 00 : 01 : 40)
         */
        function getTimeStrBySecond(second) {

            var HOUR_SECOND = 60 * 60;
            var MINUTE_SECOND = 60;

            if (second <= 0) {
                return "00:00:00";
            }
            var hours = second / HOUR_SECOND;
            if (hours > 0) {
                second -= hours * HOUR_SECOND;
            }
            var minutes = second / MINUTE_SECOND;
            if (minutes > 0) {
                second -= minutes * MINUTE_SECOND;
            }
            return (hours >= 10 ? (hours + "")
                : ("0" + hours) + ":" + (minutes >= 10 ? (minutes + "") : ("0" + minutes)) + ":"
                + (second >= 10 ? (second + "") : ("0" + second)));
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/redis/sysRedis/">查询缓存</a></li>
		<shiro:hasPermission name="redis:sysRedis:edit"><li><a href="${ctx}/redis/sysRedis/form">添加缓存</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="sysRedis" action="${ctx}/redis/sysRedis/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>数据类型：</label>
				<form:select path="dataType" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('redis_data_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>缓存键：</label>
				<form:input path="redisKey" htmlEscape="false" maxlength="200" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li><span class="help-inline">支持使用*号进行匹配查询</span></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed table-nowrap">
		<thead>
			<tr>
				<th>类型</th>
				<th>缓存键</th>
				<th>过期时间</th>
				<th>缓存值</th>
				<shiro:hasPermission name="redis:sysRedis:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="sysRedis">
		<%--<c:if test="${not empty redisValue}">--%>
			<tr>
				<td>
					${sysRedis.dataType}
				</td>
				<td>
					${sysRedis.redisKey}
				</td>
				<td>
					<c:choose>
						<c:when test="${sysRedis.expire < 0}">
							<span style="color: grey">永不过期</span>
						</c:when>
						<c:otherwise>
							<c:set var="redisExpire" value="${sysRedis.expire}" scope="page"/>
							<span style="color: red">${fns:getTimeStrBySecond(redisExpire)}</script></span>
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					${sysRedis.redisValue}
				</td>
				<shiro:hasPermission name="redis:sysRedis:edit">
					<td>
						<a href="${ctx}/redis/sysRedis/form?redisKey=${sysRedis.redisKey}">修改</a>
						<a href="${ctx}/redis/sysRedis/delete?redisKey=${sysRedis.redisKey}" onclick="return confirmx('确认要删除该缓存管理吗？', this.href)">删除</a>
					</td>
				</shiro:hasPermission>
			</tr>
		<%--</c:if>--%>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>