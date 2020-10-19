package com.aquent.crudapp.dao.person;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.aquent.crudapp.dto.Client;
import com.aquent.crudapp.dto.Person;
import com.aquent.crudapp.entity.PersonSimple;
import com.aquent.crudapp.util.CustomRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring JDBC implementation of {@link PersonDao}.
 */
@Component
public class JdbcPersonDao implements PersonDao {

    private static final String SQL_LIST_PEOPLE = "SELECT p.*, c.* FROM person p left join client c on (p.client_id = c.client_id) ORDER BY first_name, last_name, person_id";
    private static final String SQL_LIST_CONTACTS_BY_NAME = "SELECT p.*, c.* FROM person p left join client c on (p.client_id = c.client_id) WHERE c.client_name = :clientName ORDER BY first_name, last_name, person_id";
    private static final String SQL_LIST_CONTACTS_BY_ID = "SELECT p.*, c.* FROM person p left join client c on (p.client_id = c.client_id) WHERE c.client_id = :clientId ORDER BY first_name, last_name, person_id";
    private static final String SQL_READ_PERSON = "SELECT * FROM person p left join client c on (p.client_id = c.client_id) WHERE p.person_id = :personId";
    private static final String SQL_DELETE_PERSON = "DELETE FROM person WHERE person_id = :personId";
    private static final String SQL_UPDATE_PERSON = "UPDATE person SET (first_name, last_name, email_address, street_address, city, state, zip_code, client_id)"
                                                  + " = (:firstName, :lastName, :emailAddress, :streetAddress, :city, :state, :zipCode, :clientId)"
                                                  + " WHERE person_id = :personId";
    private static final String SQL_CREATE_PERSON = "INSERT INTO person (first_name, last_name, email_address, street_address, city, state, zip_code, client_id)"
                                                  + " VALUES (:firstName, :lastName, :emailAddress, :streetAddress, :city, :state, :zipCode, :clientId)";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcPersonDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Person> listPeople() {
        return namedParameterJdbcTemplate.getJdbcOperations().query(SQL_LIST_PEOPLE, CustomRowMapper.mapPerson());
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Person> listPeople(String clientName) {
        return namedParameterJdbcTemplate.query(SQL_LIST_CONTACTS_BY_NAME,
                Collections.singletonMap("clientName", clientName), CustomRowMapper.mapPerson());
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Person> listPeople(Integer clientId) {
        return namedParameterJdbcTemplate.query(SQL_LIST_CONTACTS_BY_ID,
                Collections.singletonMap("clientId", clientId), CustomRowMapper.mapPerson());
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Person readPerson(Integer personId) {
        return namedParameterJdbcTemplate.queryForObject(SQL_READ_PERSON,
                Collections.singletonMap("personId", personId), CustomRowMapper.mapPerson());
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public void deletePerson(Integer personId) {
        namedParameterJdbcTemplate.update(SQL_DELETE_PERSON, Collections.singletonMap("personId", personId));
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public void updatePerson(Person person) {
        System.out.println(person);
        System.out.println("simplePerson " + mapPerson(person));
        namedParameterJdbcTemplate.update(SQL_UPDATE_PERSON, new BeanPropertySqlParameterSource(mapPerson(person)));
        System.out.println(readPerson(person.getPersonId()));
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public Integer createPerson(Person person) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(SQL_CREATE_PERSON,
                new BeanPropertySqlParameterSource(mapPerson(person)), keyHolder);
        return keyHolder.getKey().intValue();
    }

    private PersonSimple mapPerson(Person person) {
        System.out.println(person);
        return PersonSimple.builder()
                .personId(person.getPersonId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .emailAddress(person.getEmailAddress())
                .streetAddress(person.getStreetAddress())
                .city(person.getCity())
                .state(person.getState())
                .zipCode(person.getZipCode())
                .clientId(person.getClient() == null ? null : "" + person.getClient().getClientId())
                .build();
    }
}
