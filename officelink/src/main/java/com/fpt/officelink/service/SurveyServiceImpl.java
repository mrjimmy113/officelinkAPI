package com.fpt.officelink.service;

import com.fpt.officelink.dto.AnswerDTO;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fpt.officelink.dto.AnswerOptionDTO;
import com.fpt.officelink.dto.AnswerReportDTO;
import com.fpt.officelink.dto.QuestionDTO;
import com.fpt.officelink.dto.QuestionReportDTO;
import com.fpt.officelink.dto.SurveyAnswerInforDTO;
import com.fpt.officelink.dto.SurveyDTO;
import com.fpt.officelink.dto.SurveyReportDTO;
import com.fpt.officelink.dto.TypeQuestionDTO;
import com.fpt.officelink.entity.Account;
import com.fpt.officelink.entity.Answer;
import com.fpt.officelink.entity.AnswerOption;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.SurveyQuestion;
import com.fpt.officelink.entity.SurveySendTarget;
import com.fpt.officelink.entity.WordCloud;
import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.mail.service.MailService;
import com.fpt.officelink.repository.AccountRespository;
import com.fpt.officelink.repository.AnswerOptionRepository;
import com.fpt.officelink.repository.AnswerRepository;
import com.fpt.officelink.repository.DepartmentRepository;
import com.fpt.officelink.repository.QuestionRepository;
import com.fpt.officelink.repository.SurveyQuestionRepository;
import com.fpt.officelink.repository.SurveyRepository;
import com.fpt.officelink.repository.SurveySendTargetRepository;
import com.nimbusds.jose.JOSEException;

@Service
public class SurveyServiceImpl implements SurveyService {

    private static final int PAGEMAXSIZE = 9;

    @Autowired
    SurveyRepository surveyRep;

    @Autowired
    QuestionRepository questionRep;

    @Autowired
    SurveyQuestionRepository surQuestRep;

    @Autowired
    AnswerOptionRepository optionRep;

    @Autowired
    JwtService jwtSer;

    @Autowired
    MailService mailSer;

    @Autowired
    AnswerRepository answerRep;

    @Autowired
    WordCloudFilterService filterSer;

    @Autowired
    AccountRespository accRep;

    @Autowired
    SurveySendTargetRepository targetRep;

    @Autowired
    DepartmentRepository depRep;

    @Value("${angular.path}")
    private String angularPath;

    private CustomUser getUserContext() {
        return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public boolean newSurvey(Survey survey, List<SurveyQuestion> sqList) {
        Optional<Survey> opSur = surveyRep.findByNameAndWorkplaceId(survey.getName(),
                getUserContext().getWorkplaceId());
        if (opSur.isPresent()) {
            return false;
        }
        survey.setDateCreated(new Date(Calendar.getInstance().getTimeInMillis()));
        Workplace workplace = new Workplace();
        workplace.setId(getUserContext().getWorkplaceId());
        survey.setWorkplace(workplace);
        surveyRep.save(survey);
        sqList.forEach(sq -> {
            Question q = sq.getQuestion();
            if (q.getId() == null) {
                for (AnswerOption op : q.getOptions()) {
                    op.setQuestion(q);
                }
                questionRep.save(q);
            } else {
                Optional<Question> tmp = questionRep.findById(q.getId());
                if (tmp.isPresent()) {
                    q = tmp.get();
                }
            }
            sq.setQuestion(q);
            sq.setSurvey(survey);
            surQuestRep.save(sq);
        });
        return true;

    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public boolean updateSurvey(Survey survey, List<SurveyQuestion> sqList) {
        Optional<Survey> opSur = surveyRep.findByNameAndWorkplaceId(survey.getName(),
                getUserContext().getWorkplaceId());
        if (opSur.isPresent()) {
            Optional<Survey> tmp = surveyRep.findById(survey.getId());
            if (tmp.isPresent()) {
                if (!tmp.get().getName().equalsIgnoreCase(survey.getName())) {
                    return false;
                }
            }
        }
        Optional<Survey> opCurSur = surveyRep.findById(survey.getId());
        Survey curSur = opCurSur.get();
        curSur.setName(survey.getName());
        curSur.setDateModified(new Date(Calendar.getInstance().getTimeInMillis()));
        surveyRep.save(curSur);
        surQuestRep.deleteBySurveyId(survey.getId());
        sqList.forEach(sq -> {
            Question q = sq.getQuestion();
            if (q.getId() == null) {
                for (AnswerOption op : q.getOptions()) {
                    op.setQuestion(q);
                }
                questionRep.save(q);
            } else {
                Optional<Question> tmp = questionRep.findById(q.getId());
                if (tmp.isPresent()) {
                    q = tmp.get();
                }
            }
            sq.setQuestion(q);
            sq.setSurvey(curSur);
            surQuestRep.save(sq);
        });
        return true;

    }

    @Override
    public Page<Survey> searchWithPagination(String term, int pageNum) {
        PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
        return surveyRep.findAllByNameContainingAndWorkplaceIdAndIsDeleted(term, getUserContext().getWorkplaceId(), false, pageRequest);
    }

    @Override
    public Page<Survey> searchReportWithPagination(String term, int pageNum) {
        PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
        return surveyRep.findAllByNameContainingAndWorkplaceIdAndIsDeletedAndIsSent(term,
                getUserContext().getWorkplaceId(), false, true, pageRequest);
    }

    @Override
    public void delete(Integer id) {
        Optional<Survey> opSurvey = surveyRep.findById(id);
        if (opSurvey.isPresent()) {
            Survey tmpSurvey = opSurvey.get();
            tmpSurvey.setDeleted(true);
            surveyRep.save(tmpSurvey);
        }
    }

    @Override
    public List<SurveyQuestion> getDetail(Integer id) {
        return surQuestRep.findAllBySurveyId(id);
    }

    @Override
    public SurveyDTO getTakeSurvey(String token) throws ParseException {
        SurveyDTO result = null;
        if (jwtSer.validateTakeSurveyToken(token)) {
            Integer id = jwtSer.getSurveyId(token);
            Optional<Survey> survey = surveyRep.findById(id);
            if (survey.isPresent()) {
                if (checkIfUserTakeSurvey(id)) {
                    return result;
                }
                result = new SurveyDTO();
                BeanUtils.copyProperties(survey.get(), result);
                List<SurveyQuestion> questions = surQuestRep.findAllBySurveyId(id);
                List<QuestionDTO> qDTOs = new ArrayList<QuestionDTO>();
                questions.forEach(e -> {
                    Question q = e.getQuestion();
                    QuestionDTO dto = new QuestionDTO();
                    BeanUtils.copyProperties(q, dto, "type", "options");
                    List<AnswerOptionDTO> opList = new ArrayList<AnswerOptionDTO>();
                    q.getOptions().forEach(op -> {
                        AnswerOptionDTO opDto = new AnswerOptionDTO();
                        BeanUtils.copyProperties(op, opDto);
                        opList.add(opDto);
                    });
                    dto.setOptions(opList);
                    TypeQuestionDTO typeDto = new TypeQuestionDTO();
                    BeanUtils.copyProperties(q.getType(), typeDto);
                    dto.setType(typeDto);
                    dto.setQuestionIdentity(e.getId());
                    dto.setRequired(e.isRequired());
                    qDTOs.add(dto);
                });
                result.setQuestions(qDTOs);
            }

        }
        return result;
    }

    @Override
    public String getSurveyToken(Integer surveyId) throws JOSEException {
        return jwtSer.createSurveyToken(surveyId);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public boolean sendOutSurvey(Integer surveyId, List<SurveySendTarget> targets, int duration, int workplaceId)
            throws JOSEException {
        Optional<Survey> opSurvey = surveyRep.findById(surveyId);
        if (opSurvey.isPresent()) {
            Survey survey = opSurvey.get();
            if (!survey.isSent()) {
                survey.setActive(true);
                survey.setSent(true);
                Calendar c = Calendar.getInstance();
                Date date = new Date(c.getTimeInMillis());
                survey.setDateSendOut(date);
                c.add(Calendar.DATE, duration);
                survey.setDateStop(new Date(c.getTimeInMillis()));
                Set<Account> sendList = new HashSet<Account>();
                for (SurveySendTarget target : targets) {
                    target.setSurvey(survey);
                    if (target.isNeed()) {
                        if (target.getDepartment() == null && target.getLocation() == null
                                && target.getTeam() == null) {
                            sendList.addAll(accRep.findAllByWorkplaceIdAndIsDeleted(workplaceId, false));
                        } else if (target.getDepartment() != null && target.getLocation() == null
                                && target.getTeam() == null) {
                            sendList.addAll(accRep.findAllEmailByDepartmentId(target.getDepartment().getId(),
                                    workplaceId, false));
                        } else if (target.getDepartment() == null && target.getLocation() != null
                                && target.getTeam() == null) {
                            sendList.addAll(
                                    accRep.findAllEmailByLocationId(target.getLocation().getId(), workplaceId, false));
                        } else if (target.getDepartment() != null && target.getLocation() != null
                                && target.getTeam() == null) {
                            sendList.addAll(
                                    accRep.findAllEmailByLocationIdAndDepartmentId(target.getDepartment().getId(),
                                            target.getDepartment().getId(), workplaceId, false));
                        } else if (target.getDepartment() != null && target.getLocation() != null
                                && target.getTeam() != null) {
                            sendList.addAll(accRep.findAllEmailByTeamId(target.getTeam().getId(), workplaceId, false));
                        }
                    }
                }
                if (sendList.size() == 0) {
                    return false;
                }
                String token = jwtSer.createSurveyToken(surveyId);
                List<String> emailList = new ArrayList<String>();
                sendList.forEach(e -> {
                    emailList.add(e.getEmail());
                });

                survey.setSentOut(emailList.size());
                surveyRep.save(survey);
                targetRep.saveAll(targets);
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("link", angularPath + "/take/" + token);
                mailSer.sendMail(emailList.toArray(new String[emailList.size()]), "email-survey.ftl", model);

            }

        }
        return true;

    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public void sendRoutineSurvey(int surveyId, int duration) throws JOSEException {

        Optional<Survey> survey = surveyRep.findById(surveyId);
        if (survey.isPresent()) {
            Survey templateSurvey = survey.get();
            Survey newSurvey = new Survey();
            // Set Information for Duplicate Survey
            Calendar c = Calendar.getInstance();
            Date date = new Date(c.getTimeInMillis());
            newSurvey.setName(templateSurvey.getName() + " " + date.toString());
            newSurvey.setDateCreated(date);
            newSurvey.setDateSendOut(date);
            c.add(Calendar.DATE, duration);
            newSurvey.setDateStop(new Date(c.getTimeInMillis()));
            newSurvey.setDeleted(false);
            newSurvey.setActive(true);
            newSurvey.setSent(true);
            newSurvey.setReceivedAnswer(0);
            newSurvey.setWorkplace(templateSurvey.getWorkplace());
            newSurvey.setTemplateId(templateSurvey.getId());
            List<SurveySendTarget> targets = targetRep.findAllBySurveyId(surveyId);
            int workplaceId = templateSurvey.getWorkplace().getId();

            // Find Target
            Set<Account> sendList = new HashSet<Account>();
            for (SurveySendTarget target : targets) {
                if (target.getDepartment() == null && target.getLocation() == null && target.getTeam() == null) {
                    sendList.addAll(accRep.findAllByWorkplaceIdAndIsDeleted(workplaceId, false));
                } else if (target.getDepartment() != null && target.getLocation() == null && target.getTeam() == null) {
                    sendList.addAll(
                            accRep.findAllEmailByDepartmentId(target.getDepartment().getId(), workplaceId, false));
                } else if (target.getDepartment() == null && target.getLocation() != null && target.getTeam() == null) {
                    sendList.addAll(accRep.findAllEmailByLocationId(target.getLocation().getId(), workplaceId, false));
                } else if (target.getDepartment() != null && target.getLocation() != null && target.getTeam() == null) {
                    sendList.addAll(accRep.findAllEmailByLocationIdAndDepartmentId(target.getDepartment().getId(),
                            target.getDepartment().getId(), workplaceId, false));
                } else if (target.getDepartment() != null && target.getLocation() != null && target.getTeam() != null) {
                    sendList.addAll(accRep.findAllEmailByTeamId(target.getTeam().getId(), workplaceId, false));
                }

            }
            newSurvey.setSentOut(sendList.size());
            surveyRep.save(newSurvey);
            List<SurveyQuestion> surQuests = surQuestRep.findAllBySurveyId(surveyId);
            List<SurveyQuestion> newSurQuests = new ArrayList<SurveyQuestion>();
            surQuests.forEach(e -> {
                SurveyQuestion tmp = new SurveyQuestion();
                tmp.setQuestion(e.getQuestion());
                tmp.setQuestionIndex(e.getQuestionIndex());
                tmp.setSurvey(newSurvey);
                newSurQuests.add(tmp);
            });
            surQuestRep.saveAll(newSurQuests);

            String token = jwtSer.createSurveyToken(newSurvey.getId());
            List<String> emailList = new ArrayList<String>();
            sendList.forEach(e -> {
                emailList.add(e.getEmail());
            });
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("link", angularPath + "/take/" + token);
            mailSer.sendMail(emailList.toArray(new String[emailList.size()]), "email-survey.ftl", model);
        }

    }

    @Override
    public boolean checkIfUserTakeSurvey(Integer surveyId) {
        boolean isTake = false;
        Account acc = (accRep.findByEmail(getUserContext().getUsername())).get();
        int result = answerRep.countAnswerByAccountId(acc.getId(), surveyId);

        if (result > 0) {
            isTake = true;
        }
        return isTake;
    }

    @Transactional
    @Override
    public void saveAnswer(SurveyAnswerInforDTO dto) {
        List<Answer> savedAnswer = new ArrayList<Answer>();
        Optional<Survey> opSurvey = surveyRep.findById(dto.getSurveyId());
        if (opSurvey.isPresent()) {
            Survey sur = opSurvey.get();
            sur.setReceivedAnswer(sur.getReceivedAnswer() + 1);
            surveyRep.save(sur);
            dto.getAnswers().forEach(a -> {
                Answer entity = new Answer();
                Account acc = (accRep.findByEmail(getUserContext().getUsername())).get();
                entity.setAccount(acc);
                entity.setContent(a.getContent());
                entity.setSurveyQuestion(surQuestRep.findById(a.getQuestionIdentity()).get());
                entity.setDateCreated(new Date(Calendar.getInstance().getTimeInMillis()));
                if (a.getContent().matches(".*[a-zA-Z]+.*")) {
                    entity.setWordClouds(filterSer.rawTextToWordCloud(a.getContent(), 1, entity));
                }
                savedAnswer.add(entity);
            });
            answerRep.saveAll(savedAnswer);
        }
    }

    @Override
    public SurveyReportDTO getReport(Integer id) {
        SurveyReportDTO result = new SurveyReportDTO();
        Optional<Survey> survey = surveyRep.findById(id);
        if (survey.isPresent()) {
            BeanUtils.copyProperties(survey.get(), result);
            List<SurveyQuestion> sqList = surQuestRep.findAllBySurveyId(id);
            Function<Integer, List<Answer>> method = null;
            Account acc = accRep.findByEmail(getUserContext().getUsername()).get();
            switch (acc.getRole().getRole()) {
                case "employer":
                    method = indentity -> answerRep.findAllByIndentity(indentity);
                    break;

                case "manager":
                    method = indentity -> answerRep.findAllByIndentityAndLocationIdOrDepartmentId(indentity,
                            acc.getLocation().getId(), depRep.findByAccountId(acc.getId()).get(0).getId());
                    break;
                case "employee":
                    method = indentity -> answerRep.findAllByIndentityAndDepartmentId(indentity,
                            depRep.findByAccountId(acc.getId()).get(0).getId());
                    break;
            }
            result.setQuestions(getQuestionReports(sqList, method));
        }

        return result;
    }

    @Override
    public List<Survey> getWorkplaceSurvey(int workplaceId) {

        return surveyRep.findAllByWorkplaceAndIsDeleted(workplaceId, false);
    }

    @Override
    public List<QuestionReportDTO> getFilteredReport(int surveyId, int locationId, int departmentId, int teamId) {
        List<QuestionReportDTO> result = new ArrayList<QuestionReportDTO>();
        List<SurveyQuestion> sqList = surQuestRep.findAllBySurveyId(surveyId);
        Function<Integer, List<Answer>> method = getAnswerFunction(locationId, departmentId, teamId);
        result = getQuestionReports(sqList, method);
        return result;
    }

    private Function<Integer, List<Answer>> getAnswerFunction(int locationId, int departmentId, int teamId) {
        Function<Integer, List<Answer>> method = null;
        if (locationId == 0 && departmentId == 0 && teamId == 0) {
            method = indentity -> answerRep.findAllByIndentity(indentity);
            System.out.println("ALL");
        } else if (locationId != 0 && departmentId == 0 && teamId == 0) {
            method = indentity -> answerRep.findAllByIndentityAndLocationId(indentity, locationId);
            System.out.println("Location");
        } else if (locationId == 0 && departmentId != 0 && teamId == 0) {
            method = indentity -> answerRep.findAllByIndentityAndDepartmentId(indentity, departmentId);
            System.out.println("Department");
        } else if (locationId != 0 && departmentId != 0 && teamId == 0) {
            method = indentity -> answerRep.findAllByIndentityAndLocationIdAndDepartmentId(indentity, locationId,
                    departmentId);
            System.out.println("Location - Department");
        } else if (teamId != 0) {
            method = indentity -> answerRep.findAllByIndentityAndTeamId(indentity, teamId);
            System.out.println("Team");
        }
        return method;
    }

    @Override
    public List<AnswerReportDTO> getAnswerReport(int surveyId, int questionId, int locationId, int departmentId,
            int teamId) {
        List<AnswerReportDTO> result = new ArrayList<AnswerReportDTO>();
        SurveyQuestion surveyQuestion = surQuestRep.findBySurveyIdAndQuestionId(surveyId, questionId).get();
        Function<Integer, List<Answer>> method = getAnswerFunction(locationId, departmentId, teamId);
        List<Answer> answers = method.apply(surveyQuestion.getId());
        switch (surveyQuestion.getQuestion().getType().getType()) {
            case MULTIPLE:
                result = getMutipleChoiceReport(answers);
                break;
            case SINGLE:
                result = getSingleChoiceReport(answers);
                break;
            case TEXT:
                result = getFreeTextReport(answers);
                break;
        }

        return result;
    }

    @Override
    public List<Survey> getSurveyByQuestionId(int id, int notId) {
        List<Integer> notIdList = new ArrayList<Integer>();
        notIdList.add(notId);
        return surveyRep.findAllByQuestionId(id, notIdList);
    }

    // Report: Output
    private List<QuestionReportDTO> getQuestionReports(List<SurveyQuestion> sqList,
            Function<Integer, List<Answer>> loadAnswer) {
        List<QuestionReportDTO> qrList = new ArrayList<QuestionReportDTO>();
        sqList.forEach(sq -> {
            QuestionReportDTO qr = new QuestionReportDTO();
            Question e = sq.getQuestion();
            // Change Question to Question DTO
            QuestionDTO dto = new QuestionDTO();
            BeanUtils.copyProperties(e, dto, "type", "options");
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
            // Set Question
            qr.setQuestion(dto);
            // Get each question report
            List<AnswerReportDTO> arList = new ArrayList<AnswerReportDTO>();
            List<Answer> answers = loadAnswer.apply(sq.getId());
            switch (sq.getQuestion().getType().getType()) {
                case MULTIPLE:
                    arList = getMutipleChoiceReport(answers);
                    break;
                case SINGLE:
                    arList = getSingleChoiceReport(answers);
                    break;
                case TEXT:
                    arList = getFreeTextReport(answers);
                    break;

            }
            qr.setAnswers(arList);
            qrList.add(qr);
        });
        return qrList;
    }

    // Report: Free Text
    private List<AnswerReportDTO> getFreeTextReport(List<Answer> answers) {
        List<AnswerReportDTO> result = new ArrayList<AnswerReportDTO>();
        boolean isFound;
        for (Answer a : answers) {
            for (WordCloud w : a.getWordClouds()) {
                isFound = false;
                for (AnswerReportDTO r : result) {
                    if (r.getTerm().equalsIgnoreCase(w.getWord())) {
                        r.setWeight(r.getWeight() + w.getTimes());
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    result.add(new AnswerReportDTO(w.getWord(), w.getTimes()));
                }

            }

        }
        return result;
    }

    // Report: Single choice
    private List<AnswerReportDTO> getSingleChoiceReport(List<Answer> answers) {
        List<AnswerReportDTO> result = new ArrayList<AnswerReportDTO>();
        boolean isFound;
        for (Answer answer : answers) {
            isFound = false;
            for (AnswerReportDTO AnswerReportDTO : result) {
                if (answer.getContent().equalsIgnoreCase(AnswerReportDTO.getTerm())) {
                    AnswerReportDTO.setWeight(AnswerReportDTO.getWeight() + 1);
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                result.add(new AnswerReportDTO(answer.getContent(), 1));
            }
        }
        return result;
    }

    // Report: Mutiple Choice
    private List<AnswerReportDTO> getMutipleChoiceReport(List<Answer> answers) {
        List<AnswerReportDTO> result = new ArrayList<AnswerReportDTO>();
        boolean isFound;
        for (Answer answer : answers) {
            String[] options = answer.getContent().split(",");
            for (int i = 0; i < options.length; i++) {
                isFound = false;
                for (AnswerReportDTO AnswerReportDTO : result) {
                    if (options[i].equalsIgnoreCase(AnswerReportDTO.getTerm())) {
                        AnswerReportDTO.setWeight(AnswerReportDTO.getWeight() + 1);
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    result.add(new AnswerReportDTO(options[i], 1));
                }
            }
        }
        return result;
    }

    // change active status of a survey to false
    @Override
    public Survey updateStatus(Survey survey) {
        survey.setActive(false);

        return surveyRep.save(survey);
    }

    // get all active survey with end date is today or before
    @Override
    public List<Survey> getActiveSurveyByDate(Date date) {
        List<Survey> result = surveyRep.findAllByDateStopAndIsActiveAndIsDeleted(date, true, false);

        return result;
    }

    @Override
    public Page<Survey> getHistorySurveyWithPagination(String term, int pageNum) {
        PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
        Set<Answer> answerList = answerRep.findAllByAccountId(accRep.findAccountByEmail(getUserContext().getUsername()).get().getId());
        return surveyRep.findAllByAnswersAndIsDeleted(false, term, answerList, pageRequest);
    }

    @Override
    public List<QuestionDTO> getTakeSurveyHistory(int id) {
        List<SurveyQuestion> questions = surQuestRep.findAllBySurveyId(id);
        List<QuestionDTO> qDTOs = new ArrayList<QuestionDTO>();
        questions.forEach(e -> {
            Question q = e.getQuestion();
            QuestionDTO dto = new QuestionDTO();
            BeanUtils.copyProperties(q, dto, "type", "options");
            List<AnswerOptionDTO> opList = new ArrayList<AnswerOptionDTO>();
            q.getOptions().forEach(op -> {
                AnswerOptionDTO opDto = new AnswerOptionDTO();
                BeanUtils.copyProperties(op, opDto);
                opList.add(opDto);
            });
            dto.setOptions(opList);
            TypeQuestionDTO typeDto = new TypeQuestionDTO();
            BeanUtils.copyProperties(q.getType(), typeDto);
            dto.setType(typeDto);
            dto.setQuestionIdentity(e.getId());
            qDTOs.add(dto);
        });
        return qDTOs;
    }

    @Override
    public Date getDateTakenSurvey(int surveyId) {
        Survey survey = surveyRep.findById(surveyId).get();
        Set<SurveyQuestion> setSurQues = survey.getSurveyQuestions();
        SurveyQuestion surQues = new SurveyQuestion();
        for (SurveyQuestion tmp : setSurQues) {
            surQues = tmp;
            break;
        }
        Set<Answer> setAnswer = surQues.getAnswers();
        Answer answer = new Answer();
        for (Answer tmp : setAnswer) {
            answer = tmp;
            break;
        }
        return answer.getDateCreated();
    }

    @Override
    public List<AnswerDTO> getAnswerBySurveyId(int surveyId) {
        int accountId = accRep.findAccountByEmail(getUserContext().getUsername()).get().getId();
        List<Answer> answerList = answerRep.findAllByAccountIdAndSurveyId(surveyId, accountId);
        List<AnswerDTO> answerDTO = new ArrayList<>();
        for (Answer tmp : answerList) {
            AnswerDTO dto = new AnswerDTO();
            BeanUtils.copyProperties(tmp, dto);
            answerDTO.add(dto);
        }
        return answerDTO;
    }

}
