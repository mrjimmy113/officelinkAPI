package com.fpt.officelink.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fpt.officelink.dto.AnswerOptionDTO;
import com.fpt.officelink.dto.CategoryDTO;
import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.dto.QuestionDTO;
import com.fpt.officelink.dto.TypeQuestionDTO;
import com.fpt.officelink.entity.AnswerOption;
import com.fpt.officelink.entity.Category;
import com.fpt.officelink.entity.Question;
import com.fpt.officelink.service.QuestionService;

@RestController
@RequestMapping("/question")
public class QuestionController {

	@Autowired
	QuestionService qSer;
	
	//Question
	@Secured({"ROLE_employer","ROLE_system_admin"})
	@GetMapping
	public ResponseEntity<PageSearchDTO<QuestionDTO>> search(@RequestParam("term") String term, @RequestParam("page") int page){
		HttpStatus status = null;
		PageSearchDTO<QuestionDTO> res = new PageSearchDTO<QuestionDTO>();
		
		try {
			
			Page<Question> result = qSer.searchWithPagination(term, page);
			//Convert to DTO
			List<QuestionDTO> dtoList = new ArrayList<QuestionDTO>();
			result.getContent().forEach(e ->  {
				QuestionDTO dto = new QuestionDTO();
				BeanUtils.copyProperties(e, dto,"type","options");
				List<AnswerOptionDTO> opList = new ArrayList<AnswerOptionDTO>();
				e.getOptions().forEach(op -> {
					AnswerOptionDTO opDto = new AnswerOptionDTO();
					BeanUtils.copyProperties(op, opDto);
					opList.add(opDto);
				});
				dto.setOptions(opList);
				TypeQuestionDTO typeDto = new TypeQuestionDTO();
				BeanUtils.copyProperties(e.getType(), typeDto);
				dto.setType(typeDto);
				dtoList.add(dto);
			});
			res.setMaxPage(result.getTotalPages());
			res.setObjList(dtoList);
			status = HttpStatus.OK;
		} catch (Exception e) {
			
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<PageSearchDTO<QuestionDTO>>(res, status);
	}
	
	@Secured({"ROLE_employer","ROLE_system_admin"})
	@GetMapping("/search")
	public ResponseEntity<PageSearchDTO<QuestionDTO>> searchWithType(@RequestParam("term") String term,@RequestParam("type") Integer type,@RequestParam("page") int page){
		HttpStatus status = null;
		PageSearchDTO<QuestionDTO> res = new PageSearchDTO<QuestionDTO>();
		
		try {
			
			Page<Question> result = qSer.searchWithTermAndType(term, type, page);
			//Convert to DTO
			List<QuestionDTO> dtoList = new ArrayList<QuestionDTO>();
			result.getContent().forEach(e ->  {
				QuestionDTO dto = new QuestionDTO();
				BeanUtils.copyProperties(e, dto,"type","options");
				List<AnswerOptionDTO> opList = new ArrayList<AnswerOptionDTO>();
				e.getOptions().forEach(op -> {
					AnswerOptionDTO opDto = new AnswerOptionDTO();
					BeanUtils.copyProperties(op, opDto);
					opList.add(opDto);
				});
				dto.setOptions(opList);
				TypeQuestionDTO typeDto = new TypeQuestionDTO();
				BeanUtils.copyProperties(e.getType(), typeDto);
				dto.setType(typeDto);
				dtoList.add(dto);
			});
			res.setMaxPage(result.getTotalPages());
			res.setObjList(dtoList);
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<PageSearchDTO<QuestionDTO>>(res, status);
	}
	
	@Secured({"ROLE_employer","ROLE_system_admin"})
	@PostMapping
	public ResponseEntity<Integer> createQuestion(@RequestBody QuestionDTO dto) {
		HttpStatus status = null;
		try {
			Question q = new Question();
			BeanUtils.copyProperties(dto, q,"typeId","options");
			List<AnswerOption> options = new ArrayList<AnswerOption>();
			dto.getOptions().forEach(op -> {
				AnswerOption tmp = new AnswerOption();
				BeanUtils.copyProperties(op, tmp);
				options.add(tmp);
			});
			q.setOptions(options);
			qSer.addNewQuestion(q, dto.getType().getId(), dto.getCategory().getId());
			status = HttpStatus.CREATED;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<Integer>(status.value(),status);
	}
	
	@Secured({"ROLE_employer","ROLE_system_admin"})
	@DeleteMapping
	public ResponseEntity<Integer> deleteByFlag(@RequestParam("id") Integer id) {
		HttpStatus status = null;
		try {
			qSer.deleteByFlag(id);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<Integer>(status.value(), status);
	}
	
	@Secured({"ROLE_employer","ROLE_system_admin"})
	//Type API
	@GetMapping("/type")
	public ResponseEntity<List<TypeQuestionDTO>> getAllQuestionType() {
		HttpStatus status = null;
		List<TypeQuestionDTO> result = new ArrayList<TypeQuestionDTO>();
		try {
			qSer.getAllType().forEach(t -> {
				TypeQuestionDTO tmp = new TypeQuestionDTO();
				BeanUtils.copyProperties(t, tmp);
				result.add(tmp);
			});
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<List<TypeQuestionDTO>>(result,status);
	}
	
	@Secured({"ROLE_employer","ROLE_system_admin"})
	@GetMapping("/chooseList")
	public ResponseEntity<PageSearchDTO<QuestionDTO>> getChooseQuestionList(@RequestParam("term") String term, @RequestParam("page") int page){
		HttpStatus status = null;
		PageSearchDTO<QuestionDTO> res = new PageSearchDTO<QuestionDTO>();

 		try {

 			Page<Question> result = qSer.searchWithPaginationSystemWorkplace(term, page);
			//Convert to DTO
			List<QuestionDTO> dtoList = new ArrayList<QuestionDTO>();
			result.getContent().forEach(e ->  {
				QuestionDTO dto = new QuestionDTO();
				BeanUtils.copyProperties(e, dto,"type","options");
				List<AnswerOptionDTO> opList = new ArrayList<AnswerOptionDTO>();
				e.getOptions().forEach(op -> {
					AnswerOptionDTO opDto = new AnswerOptionDTO();
					BeanUtils.copyProperties(op, opDto);
					opList.add(opDto);
				});
				dto.setOptions(opList);
				TypeQuestionDTO typeDto = new TypeQuestionDTO();
				BeanUtils.copyProperties(e.getType(), typeDto);
				dto.setType(typeDto);
				dtoList.add(dto);
			});
			res.setMaxPage(result.getTotalPages());
			res.setObjList(dtoList);
			status = HttpStatus.OK;
		} catch (Exception e) {

 			status = HttpStatus.BAD_REQUEST;
		}

 		return new ResponseEntity<PageSearchDTO<QuestionDTO>>(res, status);
	}
	
	@Secured({"ROLE_employer","ROLE_system_admin"})
	@GetMapping("/category")
	public ResponseEntity<List<CategoryDTO>> getAllCategory() {
		HttpStatus status = null;
		List<CategoryDTO> res = new ArrayList<CategoryDTO>();
		try {
			List<Category> categories = qSer.getAllCategory();
			for (Category category : categories) {
				CategoryDTO dto = new CategoryDTO();
				BeanUtils.copyProperties(category, dto);
				res.add(dto);
			}
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<List<CategoryDTO>>(res,status);
	}

}
