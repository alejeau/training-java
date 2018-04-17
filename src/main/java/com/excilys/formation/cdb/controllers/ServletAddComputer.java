package com.excilys.formation.cdb.controllers;

import com.excilys.formation.cdb.dto.model.CompanyDTO;
import com.excilys.formation.cdb.exceptions.ServiceException;
import com.excilys.formation.cdb.exceptions.ValidationException;
import com.excilys.formation.cdb.mapper.model.CompanyMapper;
import com.excilys.formation.cdb.mapper.validators.ErrorMapper;
import com.excilys.formation.cdb.model.Company;
import com.excilys.formation.cdb.model.Computer;
import com.excilys.formation.cdb.service.CompanyService;
import com.excilys.formation.cdb.service.ComputerService;
import com.excilys.formation.cdb.controllers.constants.Paths;
import com.excilys.formation.cdb.controllers.constants.Views;
import com.excilys.formation.cdb.validators.ComputerValidator;
import com.excilys.formation.cdb.validators.core.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.excilys.formation.cdb.controllers.constants.ControllerParameters.COMPANY_ID;
import static com.excilys.formation.cdb.controllers.constants.ControllerParameters.COMPANY_LIST;
import static com.excilys.formation.cdb.controllers.constants.ControllerParameters.COMPUTER_NAME;
import static com.excilys.formation.cdb.controllers.constants.ControllerParameters.CURRENT_PATH;
import static com.excilys.formation.cdb.controllers.constants.ControllerParameters.DISCONTINUED;
import static com.excilys.formation.cdb.controllers.constants.ControllerParameters.DISPLAY_SUCCESS_MESSAGE;
import static com.excilys.formation.cdb.controllers.constants.ControllerParameters.ERROR_MAP;
import static com.excilys.formation.cdb.controllers.constants.ControllerParameters.INTRODUCED;

@Controller
public class ServletAddComputer extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(ServletAddComputer.class);

    @Autowired
    private ComputerService computerService;

    @Autowired
    private CompanyService companyService;

    public ServletAddComputer() {
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.debug("doGet");
        try {
            request = setRequest(request, null, false);
        } catch (ServiceException e) {
            LOG.error("{}", e);
            throw new ServletException(e);
        }
        this.getServletContext().getRequestDispatcher(Views.ADD_COMPUTER).forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.debug("doPost");
        List<Error> errorList;

        String computerName = request.getParameter(COMPUTER_NAME);
        String introduced = request.getParameter(INTRODUCED);
        String discontinued = request.getParameter(DISCONTINUED);

        boolean displaySuccessMessage = false;
        errorList = ComputerValidator.validate(computerName, introduced, discontinued);

        try {
            if (errorList == null) {
                displaySuccessMessage = true;
                Long companyId = Long.valueOf(request.getParameter(COMPANY_ID));
                Company company = companyService.getCompany(companyId);
                Computer computer = new Computer.Builder()
                        .name(computerName)
                        .introduced(introduced)
                        .discontinued(discontinued)
                        .company(company)
                        .build();

                computerService.persistComputer(computer);
            } else {
                errorList.stream()
                        .filter(Objects::nonNull)
                        .map(Error::toString)
                        .forEach(LOG::error);
            }

            setRequest(request, errorList, displaySuccessMessage);
        } catch (ServiceException | ValidationException e) {
            LOG.error("{}", e);
            throw new ServletException(e.getMessage(), e);
        }
        this.getServletContext().getRequestDispatcher(Views.ADD_COMPUTER).forward(request, response);
    }

    private HttpServletRequest setRequest(HttpServletRequest request, List<Error> errorList, boolean displaySuccessMessage) throws ServiceException {
        LOG.debug("setRequest");
        request.setAttribute(CURRENT_PATH, Paths.PATH_ADD_COMPUTER);

        request.setAttribute(DISPLAY_SUCCESS_MESSAGE, displaySuccessMessage);
        List<CompanyDTO> companyList = CompanyMapper.mapList(companyService.getCompanies());
        request.setAttribute(COMPANY_LIST, companyList);

        Map<String, String> hashMap = ErrorMapper.toHashMap(errorList);
        request.setAttribute(ERROR_MAP, hashMap);

//        request.setAttribute(TARGET_PAGE_NUMBER, UrlMapper.mapLongNumber(request, PAGE_NB, Page.FIRST_PAGE));
//        request.setAttribute(TARGET_DISPLAY_BY, UrlMapper.mapDisplayBy(request, LimitValue.TEN).getValue());

        return request;
    }
}