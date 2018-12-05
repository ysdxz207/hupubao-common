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
import win.hupubao.common.error.ErrorInfo;
import win.hupubao.common.exception.BusinessException;

import java.io.Serializable;

/**
 * @author W.feihong
 * @date 2016年12月6日 下午9:24:25
 */
public class ResponseBase implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String CODE_FAIL = "FAIL";
	public static final String MESSAGE_FAIL = "Fail.";
	public static final int STATUS_CODE_FAIL = 300;

	public static final String ERROR_CODE_SUCCESS = "SUCCESS";
	public static final String MESSAGE_SUCCESS = "Success.";
	public static final int STATUS_CODE_SUCCESS = 200;

	private int responseStatus = STATUS_CODE_FAIL;
	private String code = CODE_FAIL;
	private String message = MESSAGE_FAIL;
	private Object data;

	public int getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(int responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	@SuppressWarnings("unchecked")
	public <T extends ResponseBase> T errorMessage(String message) {
		this.code = CODE_FAIL;
		this.message = message;
		this.responseStatus = STATUS_CODE_FAIL;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends ResponseBase> T error(Throwable e) {
		Throwable cause = e.getCause();

		if (e instanceof BusinessException || (cause instanceof BusinessException)) {
			this.message = cause == null ? e.getMessage() : cause.getMessage();
			this.code = e.getMessage();
		}

		this.responseStatus = STATUS_CODE_FAIL;
		return (T) this;
	}


	@SuppressWarnings("unchecked")
	public <T extends ResponseBase> T error(ErrorInfo error) {
		this.code = error.getErrorCode();
		this.message = error.getErrorMsg();
		this.responseStatus = STATUS_CODE_FAIL;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends ResponseBase> T success(Object data) {
		this.code = ERROR_CODE_SUCCESS;
		this.message = MESSAGE_SUCCESS;
		this.responseStatus = STATUS_CODE_SUCCESS;
		this.data = data;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends ResponseBase> T success() {
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
