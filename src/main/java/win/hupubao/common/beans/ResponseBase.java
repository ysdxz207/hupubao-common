/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *
 */
package win.hupubao.common.beans;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import win.hupubao.common.error.Error;

import java.io.Serializable;

/**
 * @author W.feihong
 * @date 2016年12月6日 下午9:24:25
 */
public class ResponseBase implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String ERROR_CODE_FAIL = "FAIL";
	public static final String MESSAGE_FAIL = "Fail.";
	public static final int STATUS_CODE_FAIL = 300;

	public static final String ERROR_CODE_SUCCESS = "SUCCESS";
	public static final String MESSAGE_SUCCESS = "Success.";
	public static final int STATUS_CODE_SUCCESS = 200;

	private int statusCode = STATUS_CODE_FAIL;
	private String errorCode = ERROR_CODE_FAIL;
	private String message = MESSAGE_FAIL;
	private Object data;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
//////////////////////////////

	/**
	 * 序列化
	 *
	 * @param data
	 */
	public void setSerializeData(Object data) {
		this.data = JSONObject.parse(JSON.toJSONString(data, SerializerFeature.DisableCircularReferenceDetect));
	}

	public ResponseBase errorMessage(String message) {
		this.errorCode = ERROR_CODE_FAIL;
		this.message = message;
		this.statusCode = STATUS_CODE_FAIL;
		return this;
	}

	public ResponseBase error(Throwable e) {
		Throwable cause = e.getCause();

		if (cause != null) {
			this.message = cause.getMessage();
			this.errorCode = e.getMessage();
		}

		this.statusCode = STATUS_CODE_FAIL;
		return this;
	}


	public ResponseBase error(Error error) {
		this.errorCode = error.getErrorCode();
		this.message = error.getErrorMsg();
		this.statusCode = STATUS_CODE_FAIL;
		return this;
	}

	public ResponseBase success(Object data) {
		this.errorCode = ERROR_CODE_SUCCESS;
		this.message = MESSAGE_SUCCESS;
		this.statusCode = STATUS_CODE_SUCCESS;
		this.data = data;
		return this;
	}

	public ResponseBase success() {
		return success(null);
	}

	/**
	 * 序列化
	 *
	 * @return
	 */
	public String serialize() {
		return JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect);
	}

	@Override
	public String toString() {
		return serialize();
	}
}
