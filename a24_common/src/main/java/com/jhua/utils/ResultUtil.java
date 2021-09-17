package com.jhua.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import com.jhua.enumeration.ResultEnum;
/**
 * http请求返回的工具类
 * @author helin01
 */
public class ResultUtil {

	/**
	 * 成功返回数据对象
	 *
	 * @param t
	 * @return
	 */
	public static <T> Result<T> success(T t) {
		Result<T> result = new Result<>();
		result.setCode(ResultEnum.SUCCESS.getCode());
		result.setMsg(ResultEnum.SUCCESS.getMsg());
		result.setData(t);
		return result;
	}

	/**
	 * 成功返回
	 *
	 * @return
	 */
	public static Result success() {
		return success(null);
	}

	/**
	 * 成功返回-带提示信息
	 *
	 * @return
	 */
	public static <T> Result<T> successWithMsg(T t, String msg) {
		Result result = success(t);
		result.setMsg(msg);
		return result;
	}

    /**
     * 成功返回-带提示信息
     *
     * @return
     */
    public static Result successWithMsg(String msg) {
        Result result = success();
        result.setMsg(msg);
        return result;
    }

	/**
	 * 失败返回
	 *
	 * @param code
	 * @param msg
	 * @return
	 */
	public static <T> Result<T> error(String code, String msg) {
		Result<T> result = new Result<>();
		result.setCode(code);
		result.setMsg(msg);
		return result;
	}

	/**
	 * 失败返回
	 *
	 * @param resultEnum
	 * @return
	 */
	public static <T> Result<T> error(ResultEnum resultEnum) {
		Result<T> result = new Result<>();
		result.setCode(resultEnum.getCode());
		result.setMsg(resultEnum.getMsg());
		return result;
	}


	/**
	 * 使用HttpServletResponse的PrintWriter返回json数据
	 *
	 * @param httpRequest
	 * @param httpResponse
	 * @param resultEnum
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	public static void writeResponseResult(HttpServletRequest httpRequest, HttpServletResponse httpResponse, ResultEnum resultEnum, Object data)
			throws IOException, JsonProcessingException {
		httpResponse.setHeader("Access-Control-Allow-Origin", httpRequest.getHeader("Origin"));
		httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
		httpResponse.setContentType("application/json; charset=utf-8");
		httpResponse.setCharacterEncoding("UTF-8");
		PrintWriter out = httpResponse.getWriter();
		ObjectMapper mapper = new ObjectMapper();

		Result result = ResultUtil.error(resultEnum);
		result.setCostMillis(0L);
		result.setData(data);
		out.println(mapper.writeValueAsString(result));
		out.flush();
		out.close();
	}
}