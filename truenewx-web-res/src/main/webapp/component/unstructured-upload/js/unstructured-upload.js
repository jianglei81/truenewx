(function($) {

	var UnstructuredUpload = function(element, options) {
		this.init(element, options);
	};

	var rpc = $.tnx.rpc.imports("unstructuredController");


	var defaultOptions = {
		authorizeType: null, // 授权类型
		maxCapacity: null, // 最大容量,以byte为单位
		progress: function(p,cpt) {} // 上传进度回调
	};

	var uploadOptions = {
		filename:null,
		resize:{
			width:0,
			height:0
		},
		crop:{
			x:0,
			y:0,
			width:0,
			height:0
		},
		successCallback:function(result){},
		errorCallback:function(error){}
	};

	UnstructuredUpload.prototype = {
		init: function(element, options) {
			this.element = element;
			this.setOptions(options);
		},
		setOptions: function(options) {
			defaultOptions = $.extend(defaultOptions, options);
		}
	};

	var getSuffix = function(fileName) {
		 var ldot = fileName.lastIndexOf(".");
		 if(ldot<0){
			 return "";
		 }
		 var type = fileName.substring(ldot);
		return type;
	}

	var bytesToSize = function(bytes) {
		if (bytes === 0) {
            return '0 B';
        }
		var k = 1024,
		sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
		i = Math.floor(Math.log(bytes) / Math.log(k));
		return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
	}

	var methods = {
		init: function(option) {
			var args = arguments,
			result = null;
			$(this).each(function(index, item) {
				var data = $.data(document, $(item).attr("id")),
				options = (typeof option !== 'object') ? null: option;
				if (!data) {
					data = new UnstructuredUpload(item, options);
					$.data(document, $(item).attr("id"), data);
					result = $.extend({
						"element": data.element
					},
					methods);
					return false;
				}
				if (typeof option === 'string') {
					if (data[option]) {
						result = data[option].apply(data, Array.prototype.slice.call(args, 1));
					} else {
						throw "Method " + option + " does not exist";
					}
				} else {
					result = data.setOptions(option);
				}
			});
			return result;
		},
		upload: function(options) {
			// 判断浏览器是否支持文件 api
			if (! (window.File || window.FileReader || window.FileList || window.Blob)) {
				throw "Browser nonsupport file api";
			}
			uploadOptions=$.extend(uploadOptions, options);
			var filename=uploadOptions.filename,
				successCallback=uploadOptions.successCallback,
				errorCallback=uploadOptions.errorCallback,
				crop=uploadOptions.crop;

			var type = defaultOptions.authorizeType,
			maxCapacity = defaultOptions.maxCapacity;
			if (!type || type == "" || type == null) {
				throw "Please set the authorization type";
			}
			var $el = !this.element ? $(this) : $(this.element);
			var files = $el.prop("files"); // 得到文件
			if (files.length == 0) {
				throw "Please select the uploaded file";
			}
			var file = files[0];
			if (maxCapacity != null && maxCapacity < file.size) {
				var size = bytesToSize(maxCapacity);
				$.tnx.alert("文件最大不能超过" + size, "上传提示");
				return;
			}

			var token = rpc.authorizePrivateWrite(type); // 请求授权
			filename = !filename || filename == "" || filename == null ? token.uuid: filename;
			var suffix = getSuffix(filename);
			var oldSuffix=getSuffix(file.name);
			if (suffix == "" || suffix == null) {
				suffix = getSuffix(file.name);
			}
			if(filename!=file.name){
				filename=filename.replace(suffix,"");
				filename=filename+oldSuffix
			}else{
				filename=filename+suffix;
			}


			if (!token) {
				throw "Request authorization failed";
			}
			var ossParam={
	                region: token.region,
	                accessKeyId: token.accessId,
	                accessKeySecret: token.accessSecret,
	                bucket: token.bucket
	            };
			if(token.tempToken&&token.tempToken!=null){
			    ossParam.stsToken=token.tempToken;
			}
			var client = new OSS.Wrapper(ossParam);
			var storeAs = token.path + filename;
			client.multipartUpload(storeAs, file, defaultOptions.progress).then(function(res) {
				var innerUrl = token.innerUrl + filename
				var protocol = window.location.protocol.replace(":");
				var imageProcess="";
				if(uploadOptions.resize&&uploadOptions.resize.width>0&&uploadOptions.resize.height>0){
					// 图片缩放
					imageProcess="/resize,m_fixed";
					imageProcess+=",w_"+uploadOptions.resize.width;
					imageProcess+=",h_"+uploadOptions.resize.height;
					imageProcess+=",limit_0";
				}
				if(uploadOptions.crop&&uploadOptions.crop.width>0&&uploadOptions.crop.height>0){
					// 图片裁剪
					imageProcess+="/crop";
					imageProcess+=",x_"+uploadOptions.crop.x+",y_"+uploadOptions.crop.y;
					imageProcess+=",w_"+uploadOptions.crop.width+",h_"+uploadOptions.crop.height;
				}
				if(imageProcess){
					// 拼接使用图片处理
					imageProcess="?x-oss-process=image"+imageProcess
				}
				if (token.publicReadable) {
					client.putACL(storeAs, 'public-read').then(function(result) {
						if (successCallback && typeof successCallback == "function") {
							if(imageProcess!=""){
								innerUrl+=imageProcess;
							}
							var outerUrl = rpc.getOuterUrl(type, innerUrl, protocol);
							var result = {
								"innerUrl": innerUrl,
								"outerUrl": outerUrl
							};
							successCallback(result);
						}
					});
				}else{
					if (successCallback  && typeof successCallback == "function") {
						if(crop!=""){
							innerUrl+=crop;
						}
						var outerUrl = rpc.getOuterUrl(type, innerUrl, protocol);
						var result = {
							"innerUrl": innerUrl,
							"outerUrl": outerUrl
						};
						successCallback(result);
					}
				}
			}).catch(function(err){
				errorCallback(err);
			}); // 上传文件
		}
	};

	$.fn.unstructuredUpload = function(method) {
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || !method) {
			return methods.init.apply(this, arguments);
		} else {
			return $.error("Method " + method + " does not exist on plug-in: unstructured-upload");
		}
	};
})(jQuery);