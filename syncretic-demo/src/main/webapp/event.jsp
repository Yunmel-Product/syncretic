<%@ page language="java" contentType="text/html; charset=UTF8" %>  
<link rel="stylesheet" href="/static/vender/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="/static/vender/datetimepicker/jquery.datetimepicker.min.css">
<style>
	.row {
     margin-right: 15px; 
     margin-left: 15px;
    }
}
</style>
<div class="row">
	<form class="form-horizontal" id="event-form" action="/events/submit" method="post">
	  <input type="hidden" name="id" id="eId" value="${e.id}">
	  <div class="form-group">
	    <label for="title" class="col-sm-2 control-label">日程内容</label>
	    <div class="col-sm-10">
	      <input type="text" class="form-control" name="title" id="title"  value="${e.title}" placeholder="请输入日程内容...">
	    </div>
	  </div>
	  
	   <div class="form-group">
	    <label for="start" class="col-sm-2 control-label">开始时间</label>
	    <div class="col-sm-10">
	      <input type="text" class="form-control datetimepicker" name="start" id="start" value="${e.start}" placeholder="请选择开始时间">
	    </div>
	  </div>
	  
	   <div class="form-group">
	    <label for="end" class="col-sm-2 control-label">结束时间</label>
	    <div class="col-sm-10">
	      <input type="text" class="form-control datetimepicker" name="end" id="end"  value="${e.end}" placeholder="请选择结束时间">
	    </div>
	  </div>
	  
	  <div class="pull-right" style=" margin-top: 80;">
		<button type="button" id="delete-btn" class="btn btn-danger col-sm-6">删除</button>
		<button type="submit" id="save-btn" class="btn btn-success col-sm-6">保存</button>
	  </div>
	</form>
</div>

<script src='http://code.jquery.com/jquery-1.9.1.js'></script>
<script type="text/javascript" src="/static/vender/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/static/vender/datetimepicker/jquery.datetimepicker.full.min.js" charset="UTF-8"></script>
<script type="text/javascript" src="/static/js/jquery.form.min.js"></script>
<script type="text/javascript">
var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
$(function(){
	$.datetimepicker.setLocale('ch');
	$('.datetimepicker').datetimepicker({
		format:"Y-m-d H:i",      //格式化日期
		yearStart:2017,     //设置最小年份
	    yearEnd:2050,        //设置最大年份
	});
	
	//提交表单
	$('#event-form').ajaxForm({
		beforeSubmit: showRequest, //表单验证
        success: showResponse //成功返回
    }); 
	
	var id = $('#eId').val();
	if(!id){
		$('#delete-btn').hide();
		$('#save-btn').text('保存');
	}else{
		$('#delete-btn').show();
		$('#save-btn').text('更新');
	}
	
	$('#delete-btn').click(function(){
		parent.layer.confirm('您确定要删除该记录吗？', {
			  btn: ['确定','取消'] //按钮
			}, function(){
			  $.post("/events/delete",{'id':$('#eId').val()},function(data){
				 parent.layer.closeAll();
				 parent.$('#calendar').fullCalendar('refetchEvents'); //重新获取所有事件数据
			  });
			}, function(){
				parent.layer.closeAll();
			})
	});
});

function showRequest(){
	var events = $("#title").val();
	if(events==''){
		alert("请输入日程内容！");
		$("#title").focus();
		return false;
	}
}

function showResponse(responseText, statusText, xhr, $form){
	if(statusText=="success"){	
		if(responseText==1){
			parent.layer.closeAll();
			parent.$('#calendar').fullCalendar('refetchEvents'); //重新获取所有事件数据
		}else{
			alert(responseText);
		}
	}else{
		alert(statusText);
	}
}

</script>