<%@ page language="java" contentType="text/html; charset=UTF8" %>  
<!DOCTYPE HTML>
<html>
<head>
<title>日程管理</title>
<link rel="stylesheet" type="text/css" href="http://www.helloweba.com/demo/css/main.css">
<link rel="stylesheet" type="text/css" href="/static/css/fullcalendar.css">
<style type="text/css">
#calendar{width:960px; margin:20px auto 10px auto}
.fancy{width:450px; height:auto}
.fancy h3{height:30px; line-height:30px; border-bottom:1px solid #d3d3d3; font-size:14px}
.fancy form{padding:10px}
.fancy p{height:28px; line-height:28px; padding:4px; color:#999}
.input{height:20px; line-height:20px; padding:2px; border:1px solid #d3d3d3; width:100px}
.btn{-webkit-border-radius: 3px;-moz-border-radius:3px;padding:5px 12px; cursor:pointer}
.btn_ok{background: #360;border: 1px solid #390;color:#fff}
.btn_cancel{background:#f0f0f0;border: 1px solid #d3d3d3; color:#666 }
.btn_del{background:#f90;border: 1px solid #f80; color:#fff }
.sub_btn{height:32px; line-height:32px; padding-top:6px; border-top:1px solid #f0f0f0; text-align:right; position:relative}
.sub_btn .del{position:absolute; left:2px}
</style>
<script src='http://code.jquery.com/jquery-1.9.1.js'></script>
<script src='http://code.jquery.com/ui/1.10.3/jquery-ui.js'></script>
<script src='/static/js/fullcalendar.min.js'></script>
<script src='/static/vender/layer/layer.js'></script>
<script type="text/javascript">
$(function() {
	$('#calendar').fullCalendar({
		header: {
			left: 'prev,next today',
			center: 'title',
			right: 'month,agendaWeek,agendaDay'
		},
		events: '/events/list',
		dayClick: function(date, allDay, jsEvent, view) {
			var selDate =$.fullCalendar.formatDate(date,'yyyy-MM-dd');
			layer.open({
			  type: 2,
			  title: "新建事件",
			  area: ['500px', '400px'],
			  shade: 0.1,
			  closeBtn: 0,
			  shadeClose: true,
			  content: '/events/add?date='+selDate
			});
    	},
    	select: function(start, end){
			console.log(start + " - " + end);
		},
		eventClick: function(event, element) {
			console.log(event);
			layer.open({
				  type: 2,
				  title: "新建事件",
				  area: ['500px', '400px'],
				  shade: 0.1,
				  closeBtn: 0,
				  shadeClose: true,
				  content: '/events/update?id=' + event.id
				});
	    },
		editable: true
	});
	
});
</script>
</head>

<body>
<div id="header">
   <div id=""><h1><a href="" title="日程管理">日程管理</a></h1></div>
</div>

<div id="main" style="width:1060px">
   <h2 class="top_title"><a href="http://www.helloweba.com/view-blog-233.html">FullCalendar应用——增删改数据操作</a></h2>
   <div id='calendar'></div>
</div>
<div id="footer">
</div>
</body>
</html>
