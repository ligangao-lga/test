package com.lga.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lga.dao.CandidateRepository;
import com.lga.dao.ResultRepository;
import com.lga.dao.UmpireRepository;
import com.lga.dao.UserRepository;
import com.lga.entity.Candidate;
import com.lga.entity.Result;
import com.lga.entity.Umpire;
import com.lga.entity.User;

@Controller
public class ResultController {
	@Autowired
	private HttpSession session;
	@Autowired
	public ResultRepository resultRepository;
	@Autowired
	public UserRepository userRepository;
	@Autowired
	public UmpireRepository umpireRepository;
	@Autowired
	public CandidateRepository candidateRepository;

	Map<String, String> RADIO_GENDER = new HashMap<>();

	@GetMapping("/resultlist") // {id}是占位符
	public String AllUser(Model model) { // @PathVariable 路径变量
		List<Result> results = resultRepository.findAll();
		model.addAttribute("results", results);
		session.setAttribute("results", results);
		return "list-result";
	}

	@GetMapping("/vote/{id}")
	public String Vote(@PathVariable("id") int id, Model model, HttpServletRequest request) {

		List<Umpire> umpires = umpireRepository.findAll();
		for (Umpire u : umpires) {
			String sss= u.getOptions()+" - "+u.getScore();
			RADIO_GENDER.put(sss,u.getOptions());
		}

		Optional<Candidate> candidate = candidateRepository.findById(id);
		Result result = new Result();
		model.addAttribute("candidate", candidate);
		model.addAttribute("umpire", RADIO_GENDER);

		result.setCandidate(candidate.get());
		model.addAttribute("result", result);
		return "list-vote";
	}

	@PostMapping("/resultvote")
	public String updateUser(Result result, Model model, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		String[] str = result.getXx();
		for (int i = 0;i<str.length;i++) {
			String s=str[i];
			Umpire umpire = umpireRepository.findByOptions(s);
		}
		//遍历数组str 得到选项名
		//通过选项名得到整条选项
		//将整条选项放选项容器里 Set<Umpire>
		//result.setUmpireSet(选项容器里);

		result.setCandidate(result.getCandidate());


		User us = (User) request.getSession().getAttribute("user");
		int id_user = us.getId();
		Optional<User> user = userRepository.findById(id_user);
		result.setUser(user.get());
		
		//result.setFs(um.getScore());
		resultRepository.save(result);
		return "redirect:/resultlist";
		// redirect是从定向
	}

	@GetMapping("/deleteresult/{id}")
	public String deleteUser(@PathVariable("id") int id, Model model) {
		Optional<Result> optional = resultRepository.findById(id);
		resultRepository.delete(optional.get());
		return "redirect:/resultlist";
	}
}
