package com.aquent.crudapp.dao.client;

import com.aquent.crudapp.dto.Client;
import com.aquent.crudapp.util.CustomRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Spring JDBC implementation of {@link ClientDao}.
 */
@Component
public class JdbcClientDao implements ClientDao {

    private static final String SQL_LIST_CLIENTS = "SELECT * FROM client ORDER BY client_name, client_id";
    private static final String SQL_READ_CLIENT_BY_ID = "SELECT * FROM client WHERE client_id = :clientId";
    private static final String SQL_READ_CLIENT_BY_NAME = "SELECT * FROM client WHERE client_name = :clientName";
    private static final String SQL_DELETE_CLIENT = "DELETE FROM client WHERE client_id = :clientId";
    private static final String SQL_UPDATE_CLIENT = "UPDATE client SET (client_name, website_uri, phone_number, street_address, city, state, zip_code)"
                                                  + " = (:clientName, :websiteUri, :phoneNumber, :streetAddress, :city, :state, :zipCode)"
                                                  + " WHERE client_id = :clientId";
    private static final String SQL_CREATE_CLIENT = "INSERT INTO client (client_name, website_uri, phone_number, street_address, city, state, zip_code)"
                                                  + " VALUES (:clientName, :websiteUri, :phoneNumber, :streetAddress, :city, :state, :zipCode)";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcClientDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Client> listClients() {
        return namedParameterJdbcTemplate.getJdbcOperations().query(SQL_LIST_CLIENTS, CustomRowMapper.mapClient());
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Client readClient(Integer clientId) {
        return namedParameterJdbcTemplate.queryForObject(SQL_READ_CLIENT_BY_ID, Collections.singletonMap("clientId", clientId), CustomRowMapper.mapClient());
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Client readClient(String clientName) {
        return namedParameterJdbcTemplate.queryForObject(SQL_READ_CLIENT_BY_NAME, Collections.singletonMap("clientName", clientName), CustomRowMapper.mapClient());
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public void deleteClient(Integer clientId) {
        namedParameterJdbcTemplate.update(SQL_DELETE_CLIENT, Collections.singletonMap("clientId", clientId));
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public void updateClient(Client client) {
        namedParameterJdbcTemplate.update(SQL_UPDATE_CLIENT, new BeanPropertySqlParameterSource(client));
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public Integer createClient(Client client) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(SQL_CREATE_CLIENT, new BeanPropertySqlParameterSource(client), keyHolder);
        return keyHolder.getKey().intValue();
    }
}
