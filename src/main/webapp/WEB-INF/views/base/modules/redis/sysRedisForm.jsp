<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <%@ include file="/WEB-INF/views/include/head.jsp" %>
    <title>缓存管理</title>
    <meta name="decorator" content="default"/>
    <style>
        .table-nowrap td {
            width: 200px;
            max-width: 175px;
            /*超出的文本加上遮罩*/
            overflow: overlay;
            white-space: nowrap;
            text-overflow: clip;
        }
    </style>
    <script type="text/javascript">
        $(document).ready(function () {
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function (form) {
                    // loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });
        });

        // 修改redis缓存名称
        var updateRedisKey = function (a) {
            var dataType = $('#dataType').val();
            var oldRedisKey = $('#oldRedisKey').val();
            var redisKey = $('#redisKey').val();
            var url = redis.URL.updateRedisKeyForm(dataType, oldRedisKey, redisKey);
            a.href = '${ctx}/' + url;
        }

        // 更新redis缓存过期时间
        var updateExpire = function (a) {
            var dataType = $('#dataType').val();
            var oldRedisKey = $('#oldRedisKey').val();
            var expire = $('#expire').val();
            var url = redis.URL.updateExpireForm(dataType, oldRedisKey, expire);
            a.href = '${ctx}/' + url;
        }

        // 修改redis缓存值
        var updateRedisValue = function (a, fromLeft) {
            var dataType = $('#dataType').val();
            var oldRedisKey = $('#oldRedisKey').val();
            var redisValue = '';
            var hashKey = '';
            var score = 0;
            if (dataType == 'string') {
                redisValue = $('#redisValue').val();
            } else if (dataType == 'list') {
                redisValue = $('#listRedisValue').val();
            } else if (dataType == 'set') {
                redisValue = $('#setRedisValue').val();
            } else if (dataType == 'zset') {
                redisValue = $('#zsetRedisValue').val();
                score = $('#score').val();
            } else if (dataType == 'hash') {
                hashKey = $('#hashKey').val();
                redisValue = $('#hashValue').val();
            } else {
                alert("00000000")
            }
            var url = redis.URL.updateRedisValueForm(dataType, oldRedisKey, redisValue, fromLeft, hashKey, score);
            a.href = '${ctx}/' + url;
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/redis/sysRedis/">查询缓存</a></li>
    <li class="active"><a href="${ctx}/redis/sysRedis/form?redisKey=${sysRedis.redisKey}"><shiro:hasPermission
            name="redis:sysRedis:edit">${not empty sysRedis.redisKey?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="redis:sysRedis:edit">查看</shiro:lacksPermission>缓存</a></li>
</ul>
<br/>
<c:choose>
    <%--如果没有数据类型--%>
    <c:when test="${empty sysRedis.dataType}">
        <form:form id="inputForm" modelAttribute="sysRedis" action="${ctx}/redis/sysRedis/form" method="post"
                   class="form-horizontal">
            <form:hidden path="id"/>
            <sys:message content="${message}"/>
            <div class="control-group">
                <label class="control-label">数据类型:</label>
                <div class="controls">
                    <form:select path="dataType" class="input-xlarge required">
                        <form:option value="" label="请选择"/>
                        <form:options items="${fns:getDictList('redis_data_type')}" itemLabel="label" itemValue="value"
                                      htmlEscape="false"/>
                    </form:select>
                    <span class="help-inline"><font color="red">*</font> </span>
                </div>
            </div>
            <div class="form-actions">
                <shiro:hasPermission name="redis:sysRedis:edit"><input id="btnSubmit" class="btn btn-primary"
                                                                       type="submit"
                                                                       value="下一步"/>&nbsp;</shiro:hasPermission>
                <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
            </div>
        </form:form>
    </c:when>
    <%--如果有数据类型--%>
    <c:otherwise>
        <form:form id="inputForm" modelAttribute="sysRedis" action="${ctx}/redis/sysRedis/save" method="post"
                   class="form-horizontal">
            <form:hidden path="id"/>
            <div class="control-group">
                <label class="control-label"><span class="help-inline"><font color="red">*</font> </span> 数据类型：</label>
                <div class="controls">
                    <form:input id="dataType" path="dataType" readonly="true" htmlEscape="false"
                                class="input-xlarge required"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label"><span class="help-inline"><font color="red">*</font> </span> Key：</label>
                <div class="controls">
                    <c:set var="oldRedisKey" value="${sysRedis.redisKey}" target="page"></c:set>
                    <input id="oldRedisKey" name="oldRedisKey" type="hidden" value="${sysRedis.redisKey}"/>
                    <form:input id="redisKey" path="redisKey" htmlEscape="false" maxlength="200"
                                class="input-xlarge required"/>
                    <c:if test="${not empty sysRedis.redisKey}">
                        <a class="btn" onclick="updateRedisKey(this)">修改名称</a>
                    </c:if>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">过期时间(秒)：</label>
                <div class="controls">
                    <form:input id="expire" path="expire" htmlEscape="false" maxlength="200"
                                class="input-xlarge redisExpire"/>
                    <c:if test="${not empty sysRedis.redisKey}">
                        <a class="btn" onclick="updateExpire(this)">更新过期时间</a>
                    </c:if>
                    <span class="help-inline">只能输入正整数/负整数,任意负数代表永不过期,输入0则无效</span>
                </div>
            </div>
            <%--如果数据类型是string--%>
            <c:if test="${sysRedis.dataType == 'string'}">
                <div class="control-group">
                    <label class="control-label"><span class="help-inline"><font color="red">*</font> </span>
                        Value：</label>
                    <div class="controls">
                        <form:textarea id="redisValue" path="redisValue"
                                       readonly="${((not empty sysRedis.redisKey) && (sysRedis.dataType != 'string'))?'true':'false'}"
                                       htmlEscape="false" rows="8" class="input-xlarge required"/>
                        <c:if test="${not empty sysRedis.redisKey}">
                            <a class="btn" onclick="updateRedisValue(this)">更新值</a>
                        </c:if>
                    </div>
                </div>
            </c:if>
            <%--如果数据类型是list--%>
            <c:if test="${sysRedis.dataType == 'list'}">
                <c:choose>
                    <c:when test="${empty sysRedis.redisKey}">
                        <div class="control-group">
                            <label class="control-label">Value：</label>
                            <div class="controls">
                                <form:textarea path="redisValue"
                                               readonly="${((not empty sysRedis.redisKey) && (sysRedis.dataType != 'string'))?'true':'false'}"
                                               htmlEscape="false" rows="8" class="input-xlarge required"/>
                                <span class="help-inline"><font color="red">*</font> </span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label">左侧/右侧添加:</label>
                            <div class="controls">
                                <form:select path="fromLeft" class="input-xlarge required">
                                    <%--<form:option value="" label="请选择"/>--%>
                                    <form:options items="${fns:getDictList('redis_list_from_left')}" itemLabel="label"
                                                  itemValue="value" htmlEscape="false"/>
                                </form:select>
                                <span class="help-inline"><font color="red">*</font> </span>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="control-group">
                            <label class="control-label">Value：</label>
                            <div class="controls">
                                <form:input id="listRedisValue" path="redisValue" htmlEscape="false" maxlength="200" class="input-xlarge"/>
                                <c:if test="${not empty sysRedis.redisKey}">
                                    <a class="btn" onclick="updateRedisValue(this, 1)">从左侧(头部)添加</a>
                                    <a class="btn" onclick="updateRedisValue(this, 0)">从右侧(尾部)添加</a>
                                </c:if>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label">值列表：</label>
                            <div class="controls">
                                <table class="table table-striped table-bordered table-condensed table-nowrap"
                                       style="width: 460px">
                                    <thead>
                                    <th>索引</th>
                                    <th>值</th>
                                    <th>操作</th>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${sysRedis.valList}" var="val" varStatus="status">
                                        <tr>
                                            <td style="width: 10px">${status.index}</td>
                                            <td>${val}</td>
                                            <td><a href="${ctx}/redis/sysRedis/deleteListValue?dataType=${sysRedis.dataType}&oldRedisKey=${sysRedis.redisKey}&currentIndex=${status.index}&redisValue=${val}" onclick="return confirmx('确认要删除该缓存吗？', this.href)">删除</a></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </c:if>
            <%--如果数据类型是set--%>
            <c:if test="${sysRedis.dataType == 'set'}">
                <c:choose>
                    <c:when test="${empty sysRedis.redisKey}">
                        <div class="control-group">
                            <label class="control-label">Value：</label>
                            <div class="controls">
                                <form:textarea path="redisValue"
                                               readonly="${((not empty sysRedis.redisKey) && (sysRedis.dataType != 'string'))?'true':'false'}"
                                               htmlEscape="false" rows="8" class="input-xlarge required"/>
                                <span class="help-inline"><font color="red">*</font> </span>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${not empty sysRedis.redisKey}">
                            <div class="control-group">
                                <label class="control-label">Value：</label>
                                <div class="controls">
                                    <form:input id="setRedisValue" path="redisValue" htmlEscape="false" maxlength="200"
                                                class="input-xlarge"/>
                                    <c:if test="${not empty sysRedis.redisKey}">
                                        <a class="btn" onclick="updateRedisValue(this)">添加值</a>
                                    </c:if>
                                </div>
                            </div>
                        </c:if>
                        <div class="control-group">
                            <label class="control-label">值列表：</label>
                            <div class="controls">
                                <table class="table table-striped table-bordered table-condensed table-nowrap"
                                       style="width: 460px">
                                    <thead>
                                    <th>索引</th>
                                    <th>值</th>
                                    <th>操作</th>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${sysRedis.valSet}" var="val" varStatus="status">
                                        <tr>
                                            <td style="width: 10px">${status.index}</td>
                                            <td>${val}</td>
                                            <td><a href="${ctx}/redis/sysRedis/deleteSetValue?dataType=${sysRedis.dataType}&oldRedisKey=${sysRedis.redisKey}&redisValue=${val}" onclick="return confirmx('确认要删除该缓存吗？', this.href)">删除</a></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </c:if>
            <%--如果数据类型是zset--%>
            <c:if test="${sysRedis.dataType == 'zset'}">
                <c:choose>
                    <c:when test="${empty sysRedis.redisKey}">
                        <div class="control-group">
                            <label class="control-label">Value：</label>
                            <div class="controls">
                                <form:textarea path="redisValue"
                                               readonly="${((not empty sysRedis.redisKey) && (sysRedis.dataType != 'string'))?'true':'false'}"
                                               htmlEscape="false" rows="8" class="input-xlarge required"/>
                                <span class="help-inline"><font color="red">*</font> </span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label">分值：</label>
                            <div class="controls">
                                <form:textarea path="score"
                                               readonly="${((not empty sysRedis.redisKey) && (sysRedis.dataType != 'string'))?'true':'false'}"
                                               htmlEscape="false" class="input-xlarge required"/>
                                <span class="help-inline"><font color="red">*</font> </span>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${not empty sysRedis.redisKey}">
                            <div class="control-group">
                                <label class="control-label">值：</label>
                                <div class="controls">
                                    <form:input id="zsetRedisValue" path="redisValue" htmlEscape="false" maxlength="200"
                                                class="input-xlarge"/>
                                    得分：<form:input id="score" path="score" htmlEscape="false" maxlength="200"
                                                class="input-xlarge"/>
                                    <c:if test="${not empty sysRedis.redisKey}">
                                        <a class="btn" onclick="updateRedisValue(this)">添加值</a>
                                    </c:if>
                                </div>
                            </div>
                        </c:if>
                        <div class="control-group">
                            <label class="control-label">值列表：</label>
                            <div class="controls">
                                <table class="table table-striped table-bordered table-condensed table-nowrap"
                                       style="width: 460px">
                                    <thead>
                                    <th>索引</th>
                                    <th>值</th>
                                    <th>分数</th>
                                    <th>操作</th>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${sysRedis.zsetList}" var="val" varStatus="status">
                                        <tr>
                                            <td style="width: 10px">${status.index}</td>
                                            <td>${val.value}</td>
                                            <td>${val.score}</td>
                                            <td><a href="${ctx}/redis/sysRedis/deleteZSetValue?dataType=${sysRedis.dataType}&oldRedisKey=${sysRedis.redisKey}&redisValue=${val.value}" onclick="return confirmx('确认要删除该缓存吗？', this.href)">删除</a></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </c:if>
            <%--如果数据类型是hash--%>
            <c:if test="${sysRedis.dataType == 'hash'}">
                <c:choose>
                    <c:when test="${empty sysRedis.redisKey}">
                        <div class="control-group">
                            <label class="control-label">HashKey：</label>
                            <div class="controls">
                                <form:input path="hashKey" readonly="${(not empty sysRedis.redisKey)?'true':'false'}"
                                            htmlEscape="false" maxlength="200" class="input-xlarge required"/>
                                <span class="help-inline"><font color="red">*</font> </span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label">HashValue：</label>
                            <div class="controls">
                                <form:textarea path="redisValue"
                                               readonly="${((not empty sysRedis.redisKey) && (sysRedis.dataType != 'string'))?'true':'false'}"
                                               htmlEscape="false" rows="8" class="input-xlarge required"/>
                                <span class="help-inline"><font color="red">*</font> </span>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="control-group">
                            <label class="control-label">Hash键：</label>
                            <div class="controls">
                                <form:input id="hashKey" path="hashKey" htmlEscape="false" maxlength="200" class="input-xlarge required"/>
                                <span>Hash值：</span>
                                <form:input id="hashValue" path="redisValue" htmlEscape="false" class="input-xlarge required"/>
                                <a class="btn" onclick="updateRedisValue(this)">添加值</a>
                                <span class="help-inline"><font color="red">*</font> </span>
                            </div>
                        </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label">值列表：</label>
                            <div class="controls">
                                <table class="table table-striped table-bordered table-condensed table-nowrap"
                                       style="width: 460px">
                                    <thead>
                                    <tr>
                                        <th>索引</th>
                                        <th>HashKey</th>
                                        <th>HashValue</th>
                                        <th>操作</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${sysRedis.valMap}" var="val" varStatus="status">
                                        <tr>
                                            <td>${status.index}</td>
                                            <td>${val.key}</td>
                                            <td>${val.value}</td>
                                            <td><a href="${ctx}/redis/sysRedis/deleteHash?dataType=${sysRedis.dataType}&oldRedisKey=${sysRedis.redisKey}&hashKey=${val.key}" onclick="return confirmx('确认要删除该缓存吗？', this.href)">删除</a></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </c:if>
            <c:if test="${empty sysRedis.redisKey}">
                <div class="form-actions">
                    <shiro:hasPermission name="redis:sysRedis:edit">
                        <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
                    </shiro:hasPermission>
                    <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
                </div>
            </c:if>
        </form:form>
    </c:otherwise>
</c:choose>

<script>
    var redis = {
        /**与服务端交互url*/
        URL: {
            updateRedisKeyForm: function (dataType, oldRedisKey, redisKey) {
                return '/redis/sysRedis/updateRedisKey?dataType=' + dataType +'&oldRedisKey=' + oldRedisKey + '&redisKey=' + redisKey;
            },
            updateExpireForm: function (dataType, oldRedisKey, expire) {
                return '/redis/sysRedis/updateExpire?dataType=' + dataType + '&oldRedisKey=' + oldRedisKey + '&expire=' + expire;
            },
            updateRedisValueForm: function (dataType, oldRedisKey, redisValue, fromLeft, hashKey, score) {
                return '/redis/sysRedis/updateRedisValue?dataType=' + dataType + '&oldRedisKey=' + oldRedisKey + '&redisValue=' + redisValue + '&fromLeft=' + fromLeft + '&hashKey=' + hashKey+ '&score=' + score;
            }
        }
    }
</script>
</body>
</html>