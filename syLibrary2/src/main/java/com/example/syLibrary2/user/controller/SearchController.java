package com.example.syLibrary2.user.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.example.syLibrary2.admin.model.dto.BookDTO;
import com.example.syLibrary2.user.model.dao.CheckoutDAO;
import com.example.syLibrary2.user.model.dao.SearchDAO;
import com.example.syLibrary2.util.PageUtil;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("user/search/*")
public class SearchController {
	@Autowired
	SearchDAO searchDao;

	@Autowired
	CheckoutDAO checkoutDao;

	@GetMapping("/") // 소장자료검색 페이지로 이동
	public String search() {
		return "/user/search/search";
	}

	@PostMapping("result")
	public ModelAndView searchResult(@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "option", defaultValue = "all") String option,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			@RequestParam(name = "view", defaultValue = "view1") String view) {
		// 페이지 설정
		int count = searchDao.resultCount(option, keyword);
		PageUtil pageInfo = new PageUtil(count, page);
		int start = pageInfo.getPageBegin();
		int end = pageInfo.getPageEnd();

		// 검색 결과 리스트
		List<BookDTO> list = searchDao.totSearch(keyword, start, end);

		ModelAndView mav = new ModelAndView();
		mav.setViewName("user/search/searchResult");
		mav.addObject("list", list);
		mav.addObject("stateinfo", searchDao.listState(list)); // 대출관련 상태정보 리스트
		mav.addObject("cntRec", searchDao.countRecords(keyword, keyword, keyword));
		mav.addObject("count", count);
		mav.addObject("keyword", keyword);
		mav.addObject("page", pageInfo);
		mav.addObject("view", view);
		mav.addObject("option", option);
		return mav;
	}

	@PostMapping("detailSearch")
	public ModelAndView detailSearch(@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "option", defaultValue = "detail") String option,
			@RequestParam(name = "b_name", defaultValue = "") String b_name,
			@RequestParam(name = "b_author", defaultValue = "") String b_author,
			@RequestParam(name = "b_pub", defaultValue = "") String b_pub,
			@RequestParam(name = "view", defaultValue = "view1") String view) {

		// 페이지 설정
		int count = searchDao.resultCount(option, b_name, b_author, b_pub);
		PageUtil pageInfo = new PageUtil(count, page);
		int start = pageInfo.getPageBegin();
		int end = pageInfo.getPageEnd();

		List<BookDTO> list = searchDao.detailSearch(b_name, b_author, b_pub, start, end);

		ModelAndView mav = new ModelAndView();
		mav.setViewName("user/search/detailResult");
		mav.addObject("list", list);
		mav.addObject("stateinfo", searchDao.listState(list)); // 대출관련 상태정보 리스트
		mav.addObject("cntRec", searchDao.countRecords(b_name, b_author, b_pub));
		mav.addObject("count", count);
		mav.addObject("b_name", b_name);
		mav.addObject("b_author", b_author);
		mav.addObject("b_pub", b_pub);
		mav.addObject("page", pageInfo);
		mav.addObject("view", view);
		mav.addObject("option", option);

		return mav;
	}

	@RequestMapping("moveTo")
	public ModelAndView moveTo(@RequestParam(name = "page") int page, @RequestParam(name = "option") String option,
			@RequestParam(name = "view", defaultValue = "view1") String view, HttpServletRequest request) {
		// 파라미터 초기화
		List<BookDTO> list = null;
		List<Map<String, Object>> stateinfo = null;
		int count, curPage, start, end = 0;
		PageUtil pageInfo = null;
		String resultPage = "";
		ModelAndView mav = new ModelAndView();

		if (view.equals("view1")) {
			resultPage = "/user/search/view1";
		} else if (view.equals("view2")) {
			resultPage = "/user/search/view2";
		}

		switch (option) {
		case "all":
			// 사용자가 입력한 검색어
			String keyword = request.getParameter("keyword");

			// 페이지 설정
			count = searchDao.resultCount(option, keyword);
			pageInfo = new PageUtil(count, page);
			start = pageInfo.getPageBegin();
			end = pageInfo.getPageEnd();

			// 검색 결과 목록 가져오기
			list = searchDao.totSearch(keyword, start, end);
			System.out.println(list);
			mav.setViewName(resultPage);
			mav.addObject("list", list);
			mav.addObject("stateinfo", searchDao.listState(list));
			mav.addObject("count", count);
			mav.addObject("keyword", keyword);
			mav.addObject("page", pageInfo);
			mav.addObject("option", option);

			break;
		case "detail":
			String b_name = (request.getParameter("b_name") != null) ? request.getParameter("b_name") : "";
			String b_author = (request.getParameter("b_author") != null) ? request.getParameter("b_author") : "";
			String b_pub = (request.getParameter("b_pub") != null) ? request.getParameter("b_pub") : "";

			// 페이지 설정
			count = searchDao.resultCount(option, b_name, b_author, b_pub);
			pageInfo = new PageUtil(count, page);
			start = pageInfo.getPageBegin();
			end = pageInfo.getPageEnd();

			// 검색 결과 목록 가져오기
			list = searchDao.detailSearch(b_name, b_author, b_pub, start, end);
			mav.setViewName(resultPage);
			mav.addObject("list", list);
			mav.addObject("stateinfo", searchDao.listState(list));
			mav.addObject("count", count);
			mav.addObject("b_name", b_name);
			mav.addObject("b_author", b_author);
			mav.addObject("b_pub", b_pub);
			mav.addObject("page", pageInfo);
			mav.addObject("option", option);
			break;
		}
		return mav;
	}

	@RequestMapping("searchBy")
	public ModelAndView searchBy(@RequestParam(name = "searchOpt") String option,
			@RequestParam(name = "b_name", defaultValue = "") String b_name,
			@RequestParam(name = "b_author", defaultValue = "") String b_author,
			@RequestParam(name = "b_pub", defaultValue = "") String b_pub,
			@RequestParam(name = "view", defaultValue = "view1") String view,
			@RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "count") int count) {
		System.out.println("test = " + option);
		String resultPage = "";
		if (view.equals("view1")) {
			resultPage = "/user/search/view1";
		} else if (view.equals("view2")) {
			resultPage = "/user/search/view2";
		}

		// 페이지 설정
		PageUtil pageInfo = new PageUtil(count, page);
		int start = pageInfo.getPageBegin();
		int end = pageInfo.getPageEnd();

		List<BookDTO> list = searchDao.detailSearch(b_name, b_author, b_pub, start, end);
		ModelAndView mav = new ModelAndView();
		mav.setViewName(resultPage);
		mav.addObject("list", list);
		mav.addObject("stateinfo", searchDao.listState(list));
		mav.addObject("cntRec", searchDao.countRecords(b_name, b_author, b_pub));
		mav.addObject("count", count);
		mav.addObject("b_name", b_name);
		mav.addObject("b_author", b_author);
		mav.addObject("b_pub", b_pub);
		mav.addObject("page", pageInfo);
		mav.addObject("view", view);
		mav.addObject("option", option);
		return mav;
	}

	@GetMapping("bookInfo/{b_id}")
	public ModelAndView detail(@PathVariable(name = "b_id") int b_id, ModelAndView mav) {
		// 도서 상세정보 페이지
		mav.setViewName("user/search/bookinfo");
		mav.addObject("map", searchDao.showDetails(b_id));
		mav.addObject("l_retdate", searchDao.fastRetdate(b_id));
		mav.addObject("state", checkoutDao.isAvailable(b_id));
		return mav;
	}

	@ResponseBody
	@RequestMapping("autocomplete")
	public Map<String, Object> autocomplete(@RequestParam(name = "keyword", defaultValue = "") String keyword) {
		// 도서 제목 자동완성
		Map<String, Object> params = new HashMap();
		List<Map<String, Object>> arrResult = new ArrayList<>();
		params.put("keyword", keyword);

		// 도서명, 저자, 출판사 기준 레코드 검색
		params.put("searchOpt", "B_NAME");
		arrResult.addAll(searchDao.autocomplete(params));

		params.put("searchOpt", "B_AUTHOR");
		arrResult.addAll(searchDao.autocomplete(params));

		params.put("searchOpt", "B_PUB");
		arrResult.addAll(searchDao.autocomplete(params));

		// 파라미터에 결과데이터 저장
		params.put("arrResult", arrResult);
		return params;
	}
}
