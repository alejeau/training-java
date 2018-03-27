package com.excilys.formation.cdb.paginator;

import com.excilys.formation.cdb.exceptions.ServiceException;
import com.excilys.formation.cdb.model.Company;
import com.excilys.formation.cdb.paginator.core.LimitValue;
import com.excilys.formation.cdb.paginator.core.Page;
import com.excilys.formation.cdb.service.CompanyService;

import javax.sql.rowset.serial.SerialException;

public class CompanyPage extends Page<Company> {

    public CompanyPage() {
        super();
    }

    public CompanyPage(LimitValue limit) {
        super(limit);
    }

    @Override
    public Long currentLastPageNumber() throws ServiceException {
        Long numberOfCompany = CompanyService.INSTANCE.getNumberOfCompanies();
        return numberOfCompany / this.limit.getValue();
    }

    @Override
    protected void refresh(long offset)throws ServiceException {
        this.page = CompanyService.INSTANCE.getCompanies(offset, this.limit.getValue());
    }
}
