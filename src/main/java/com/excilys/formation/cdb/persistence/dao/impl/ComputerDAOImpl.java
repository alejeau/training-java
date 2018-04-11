package com.excilys.formation.cdb.persistence.dao.impl;

import com.excilys.formation.cdb.exceptions.DAOException;
import com.excilys.formation.cdb.exceptions.MapperException;
import com.excilys.formation.cdb.mapper.model.ComputerMapper;
import com.excilys.formation.cdb.model.Computer;
import com.excilys.formation.cdb.persistence.DatabaseField;
import com.excilys.formation.cdb.persistence.dao.ComputerDAO;
import com.excilys.formation.cdb.persistence.dao.SimpleDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.excilys.formation.cdb.persistence.dao.impl.ComputerDAORequest.DELETE_COMPUTER;
import static com.excilys.formation.cdb.persistence.dao.impl.ComputerDAORequest.DIRECTION;
import static com.excilys.formation.cdb.persistence.dao.impl.ComputerDAORequest.INSERT_COMPUTER;
import static com.excilys.formation.cdb.persistence.dao.impl.ComputerDAORequest.NUMBER_OF_COMPUTERS;
import static com.excilys.formation.cdb.persistence.dao.impl.ComputerDAORequest.NUMBER_OF_COMPUTERS_WITH_NAME_OR_COMPANY_NAME;
import static com.excilys.formation.cdb.persistence.dao.impl.ComputerDAORequest.ORDER_FIELD;
import static com.excilys.formation.cdb.persistence.dao.impl.ComputerDAORequest.SELECT_ALL_COMPUTERS_ORDERED_BY;
import static com.excilys.formation.cdb.persistence.dao.impl.ComputerDAORequest.SELECT_COMPUTER_BY_ID;
import static com.excilys.formation.cdb.persistence.dao.impl.ComputerDAORequest.SELECT_COMPUTER_BY_NAME_OR_COMPANY_NAME_ORDERED_BY;
import static com.excilys.formation.cdb.persistence.dao.impl.ComputerDAORequest.UPDATE_COMPUTER;

@Repository
public class ComputerDAOImpl implements ComputerDAO {
    private static final Logger LOG = LoggerFactory.getLogger(ComputerDAOImpl.class);

    private static final String ASCENDING = "ASC";
    private static final String DESCENDING = "DESC";

    private DataSource dataSource;
    private SimpleDAO simpleDAO;

    ComputerDAOImpl() {
    }

    @Autowired
    public ComputerDAOImpl(SimpleDAO simpleDAO, DataSource dataSource) {
        this.simpleDAO = simpleDAO;
        this.dataSource = dataSource;
    }

    @Override
    public Long getNumberOfComputers() throws DAOException {
        LOG.debug("getNumberOfComputers");
        return simpleDAO.count(NUMBER_OF_COMPUTERS);
    }

    @Override
    public Long getNumberOfComputersWithName(String name) throws DAOException {
        LOG.debug("getNumberOfComputersWithName");
        return simpleDAO.countWithStringParameters(NUMBER_OF_COMPUTERS_WITH_NAME_OR_COMPANY_NAME, Arrays.asList(name, name));
    }

    @Override
    public Computer getComputer(Long id) throws DAOException {
        LOG.debug("getComputersWithName");
        Connection conn = this.getConnection();
        PreparedStatement prepStmt = null;
        ResultSet rs = null;
        Computer c;

        try {
            prepStmt = conn.prepareStatement(SELECT_COMPUTER_BY_ID);
            prepStmt.setLong(1, id);

            LOG.debug("Executing query \"{}\"", prepStmt);
            rs = prepStmt.executeQuery();
            try {
                c = ComputerMapper.map(rs);
            } catch (MapperException e) {
                LOG.error("{}", e);
                throw new DAOException("Error while mapping the ResultSet!", e);
            }
        } catch (SQLException e) {
            LOG.error("{}", e);
            throw new DAOException("Couldn't get computer with ID " + id + "!", e);
        } finally {
            DAOUtils.closeElements(conn, prepStmt, rs);
        }

        LOG.debug("Returning {}", c);
        return c;
    }

    @Override
    public List<Computer> getComputersWithName(String name, long index, Long limit) throws DAOException {
        LOG.debug("getComputersWithName");
        return getComputersWithNameOrderedBy(name, index, limit, DatabaseField.COMPUTER_NAME, true);
    }

    @Override
    public List<Computer> getComputersWithNameOrderedBy(String name, long index, Long limit, DatabaseField computerField, boolean ascending) throws DAOException {
        LOG.debug("getComputersWithNameOrderedBy");
        Connection conn = this.getConnection();
        PreparedStatement prepStmt = null;
        ResultSet rs = null;
        List<Computer> computers;
        final String QUERY = SELECT_COMPUTER_BY_NAME_OR_COMPANY_NAME_ORDERED_BY
                .replace(ORDER_FIELD, computerField.getValue())
                .replace(DIRECTION, ascending ? ASCENDING : DESCENDING);
        LOG.debug("Query = {}", QUERY);
        try {
            prepStmt = conn.prepareStatement(QUERY);
            prepStmt.setString(1, "%" + name + "%");
            prepStmt.setString(2, "%" + name + "%");
            prepStmt.setLong(3, index);
            prepStmt.setLong(4, limit);

            LOG.debug("Executing query \"{}\"", prepStmt);
            rs = prepStmt.executeQuery();
            try {
                computers = ComputerMapper.mapList(rs);
            } catch (MapperException e) {
                LOG.error("{}", e);
                throw new DAOException("Error while mapping the ResultSet!", e);
            }
        } catch (SQLException e) {
            LOG.error("{}", e);
            throw new DAOException("Couldn't get list of computers with NAME LIKE " + name + "!", e);
        } finally {
            DAOUtils.closeElements(conn, prepStmt, rs);
        }

        LOG.debug("Returning list of size {}", computers.size());
        return computers;
    }

    @Override
    public List<Computer> getComputerList(long index, Long limit) throws DAOException {
        return getComputerListOrderedBy(index, limit, DatabaseField.COMPUTER_NAME, true);
    }

    @Override
    public List<Computer> getComputerListOrderedBy(long index, Long limit, DatabaseField computerField, boolean ascending) throws DAOException {
        LOG.debug("getComputerList");
        Connection conn = this.getConnection();
        PreparedStatement prepStmt = null;
        ResultSet rs = null;
        List<Computer> computers;
        final String QUERY = SELECT_ALL_COMPUTERS_ORDERED_BY
                .replace(ORDER_FIELD, computerField.getValue())
                .replace(DIRECTION, ascending ? ASCENDING : DESCENDING);

        try {
            prepStmt = conn.prepareStatement(QUERY);
            prepStmt.setLong(1, index);
            prepStmt.setLong(2, limit);

            LOG.debug("Executing query \"{}\"", prepStmt);
            rs = prepStmt.executeQuery();

            try {
                computers = ComputerMapper.mapList(rs);
            } catch (MapperException e) {
                LOG.error("{}", e);
                throw new DAOException("Error while mapping the ResultSet!", e);
            }
        } catch (SQLException e) {
            LOG.error("{}", e);
            throw new DAOException("Couldn't get list of computers from " + index + " to " + limit + "!", e);
        } finally {
            DAOUtils.closeElements(conn, prepStmt, rs);
        }

        LOG.debug("Returning list of size {}", computers.size());
        return computers;
    }


    @Override
    public Long persistComputer(Computer computer) throws DAOException {
        LOG.debug("persistComputer");
        Connection conn = this.getConnection();
        PreparedStatement prepStmt = null;
        ResultSet rs = null;
        Long createdId = null;

        try {
            prepStmt = conn.prepareStatement(INSERT_COMPUTER, Statement.RETURN_GENERATED_KEYS);
            prepStmt.setString(1, computer.getName());
            if (computer.getIntroduced() != null) {
                prepStmt.setDate(2, Date.valueOf(computer.getIntroduced()));
            } else {
                prepStmt.setNull(2, Types.DATE);
            }

            if (computer.getDiscontinued() != null) {
                prepStmt.setDate(3, Date.valueOf(computer.getDiscontinued()));
            } else {
                prepStmt.setNull(3, Types.DATE);
            }
            if (computer.getCompany() != null && computer.getCompany().getId() != null) {
                prepStmt.setLong(4, computer.getCompany().getId());
            } else {
                prepStmt.setNull(4, Types.BIGINT);
            }
            prepStmt.executeUpdate();

            LOG.debug("Executing query \"{}\"", prepStmt);
            rs = prepStmt.getGeneratedKeys();
            if (rs.next()) {
                createdId = rs.getLong(1);
            }
        } catch (SQLException e) {
            LOG.error("{}", e);
            throw new DAOException("Couldn't persist the computer " + computer.shortToString() + ".", e);
        } finally {
            DAOUtils.closeElements(conn, prepStmt, rs);
        }

        LOG.debug("Returning id {}", createdId);
        return createdId;
    }

    @Override
    public void updateComputer(Computer computer) throws DAOException {
        LOG.debug("updateComputer");
        LOG.debug("Computer: {}", computer);
        Connection conn = this.getConnection();
        PreparedStatement prepStmt = null;

        try {
            prepStmt = conn.prepareStatement(UPDATE_COMPUTER);
            prepStmt.setString(1, computer.getName());
            if (computer.getIntroduced() != null) {
                prepStmt.setDate(2, Date.valueOf(computer.getIntroduced()));
            } else {
                prepStmt.setNull(2, Types.DATE);
            }
            if (computer.getDiscontinued() != null) {
                prepStmt.setDate(3, Date.valueOf(computer.getDiscontinued()));
            } else {
                prepStmt.setNull(3, Types.DATE);
            }
            if (computer.getCompany() != null && computer.getCompany().getId() != null) {
                prepStmt.setLong(4, computer.getCompany().getId());
            } else {
                prepStmt.setNull(4, Types.BIGINT);
            }
            prepStmt.setLong(5, computer.getId());

            LOG.debug("Executing query \"{}\"", prepStmt);
            prepStmt.executeUpdate();
        } catch (SQLException e) {
            LOG.error("{}", e);
            throw new DAOException("Couldn't update the computer with ID " + computer.getId() + ".", e);
        } finally {
            DAOUtils.closeElements(conn, prepStmt, null);
        }
    }

    @Override
    @Transactional
    public void deleteComputer(Long id) throws DAOException {
        LOG.debug("deleteComputer");
        this.deleteComputers(Collections.singletonList(id));
    }

    @Override
    @Transactional
    public void deleteComputers(List<Long> idList) throws DAOException {
        LOG.debug("deleteComputers (by list)");
        Connection connection = this.getConnection();

        try (PreparedStatement prepStmt = connection.prepareStatement(DELETE_COMPUTER)) {
            for (Long id : idList) {
                prepStmt.setLong(1, id);
                LOG.debug("Executing query \"{}\"", prepStmt);
                prepStmt.executeUpdate();
            }
        } catch (SQLException e) {
            LOG.error("{}", e);
            throw new DAOException("Couldn't delete the supplied list of computers.", e);
        } finally {
            DAOUtils.closeElements(connection, null, null);
        }
    }

    private Connection getConnection() throws DAOException {
        Connection conn;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            LOG.error("{}", e);
            throw new DAOException("Couldn't obtain a connection!", e);
        }
        return conn;
    }
}
