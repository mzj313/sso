var rulesRegObj = ['username','password','captcha'];
var errorinfoObj = {
    'username':{'n':"用户名不能为空",'f':"用户名有字母和数字组成"},
    'password':{'n':"密码不能为空",'f':"密码由6-16位字符组成"},
    'captcha':{'n':"验证码不能为空",'f':"验证码为5位"}
};

var rulesObj = {    
    'username' : /^[a-zA-Z0-9_\-.]{1,}@[a-zA-Z0-9_\-]{1,}\.[a-zA-Z0-9_\-.]{1,}$/,
    'password' : /^([a-zA-Z0-9]{6,16})$/,
    'captcha' : /^([a-zA-Z0-9]{5,5})$/
};

var validateLoginForm = function(){
    var isPass = false;
    for(var i=0;i<rulesRegObj.length;i++){
        if(!checkInputValue(rulesRegObj[i])){
            return isPass;
        }
    }    
    isPass = true;
    return isPass;
}

var winWidth = $(window).width();
if(winWidth < 640){
    $(".bottom-row").css("width",winWidth * .87+"px");
    $(".loginscreen").css("width",winWidth * .87+"px");
    $(".m-t-bottom").css("width",winWidth * .87+"px");
}


//$(".form-group input").on("keyup",function(){
//    validateLoginForm();
//});

var checkInputValue = function(id){
    var _id = id,flag = false;
    var _inputVal = $("#"+_id).val();
    if(trimBlank(_inputVal,'g') == ""){//若为空
        showErrorInfo(_id,errorinfoObj[_id].n);
        return flag;
    }
//    else if(!rulesObj[_id].test(_inputVal)){//若不为空,校验不通过
//        showErrorInfo(_id,errorinfoObj[_id].f);
//        return flag;
//    }    
    clearErrorInfo(_id);
    flag = true;
    return flag;
}

var showErrorInfo = function(id,errorInfo){
    $("#"+id+"-error").text(errorInfo);
    $("#"+id+"-error").show();
}

var clearErrorInfo = function(id){
    $("#"+id+"-error").hide();
}

function trimBlank(str,is_global)
{
    var result;
    result = str.replace(/(^\s+)|(\s+$)/g,"");
    if(is_global.toLowerCase()=="g")
    {
        result = result.replace(/\s/g,"");
    }
    return result;
}