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
import win.hupubao.common.utils.BusinessExceptionUtils;
import win.hupubao.common.utils.StringUtils;

import java.io.Serializable;

/**
 * @author ysdxz207
 * @date 2016年12月6日 下午9:24:25
 */
public class ResponseBase implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String RESPONSE_CODE_FAIL = "FAIL";
	public static final String MESSAGE_FAIL = "Fail.";
	public static final int RESPONSE_STATUS_FAIL = 300;

	public static final String ERROR_CODE_SUCCESS = "SUCCESS";
	public static final String MESSAGE_SUCCESS = "Success.";
	public static final int STATUS_CODE_SUCCESS = 200;

	private int responseStatus = RESPONSE_STATUS_FAIL;
	private String responseCode = RESPONSE_CODE_FAIL;
	private String message = MESSAGE_FAIL;
	private Object data;

	public int getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(int responseStatus) {
		this.responseStatus = responseStatus;
	}


	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
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
		this.responseCode = RESPONSE_CODE_FAIL;
		this.message = message;
		this.responseStatus = RESPONSE_STATUS_FAIL;
		return (T) this;
	}

	public <T extends ResponseBase> T error(Throwable e) {
		return error(e, MESSAGE_FAIL);
	}

	@SuppressWarnings("unchecked")
	public <T extends ResponseBase> T error(Throwable e, String defaultMessage) {
		BusinessException businessException = getBusinessException(e, defaultMessage);
		this.responseCode = businessException.getCode();
		this.message = businessException.getMessage();
		this.responseStatus = RESPONSE_STATUS_FAIL;
		return (T) this;
	}

	/**
	 * 递归获取最底层BusinessException
	 * @param e
	 * @param defaultMessage
	 * @return
	 */
	private BusinessException getBusinessException(Throwable e, String defaultMessage) {
		BusinessException businessException = BusinessExceptionUtils.getBusinessException(e);
		if (businessException == null) {
			return new BusinessException(RESPONSE_CODE_FAIL, StringUtils.isEmpty(defaultMessage) ? MESSAGE_FAIL: defaultMessage);
		}

		return businessException;
	}


	@SuppressWarnings("unchecked")
	public <T extends ResponseBase> T error(ErrorInfo error) {
		this.responseCode = error.getErrorCode();
		this.message = error.getErrorMsg();
		this.responseStatus = RESPONSE_STATUS_FAIL;
		return (T) this;
	}

	public <T extends ResponseBase> T success(Object data) {
		return success(MESSAGE_SUCCESS, data);
	}

	public <T extends ResponseBase> T success() {
		return success(null);
	}

	@SuppressWarnings("unchecked")
	public <T extends ResponseBase> T success(String message, Object data) {
		this.responseCode = ERROR_CODE_SUCCESS;
		this.message = message;
		this.responseStatus = STATUS_CODE_SUCCESS;
		this.data = data;
		return (T) this;
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
