package com.aquent.crudapp.util;

import com.aquent.crudapp.dto.Client;
import com.aquent.crudapp.dto.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomRowMapper {

    public static PersonRowMapper mapPerson() {
        return new PersonRowMapper();
    }

    public static ClientRowMapper mapClient() {
        return new ClientRowMapper();
    }


    /**
     * Row mapper for client records.
     */
    private static final class ClientRowMapper implements RowMapper<Client> {

        @Override
        public Client mapRow(ResultSet rs, int rowNum) throws SQLException {
            Client client = new Client();
            client.setClientId(rs.getInt("client_id"));
            client.setClientName(rs.getString("client_name"));
            client.setWebsiteUri(rs.getString("website_uri"));
            client.setPhoneNumber(rs.getString("phone_number"));
            client.setStreetAddress(rs.getString("street_address"));
            client.setCity(rs.getString("city"));
            client.setState(rs.getString("state"));
            client.setZipCode(rs.getString("zip_code"));
            return client;
        }
    }


    private static final class PersonRowMapper implements RowMapper<Person> {

        @Override
        public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
            Person person = new Person();
            person.setPersonId(rs.getInt("person_id"));
            person.setFirstName(rs.getString("first_name"));
            person.setLastName(rs.getString("last_name"));
            person.setEmailAddress(rs.getString("email_address"));
            person.setStreetAddress(rs.getString("street_address"));
            person.setCity(rs.getString("city"));
            person.setState(rs.getString("state"));
            person.setZipCode(rs.getString("zip_code"));
            System.out.println(rs.getString("first_name") + " " + rs.getString("client_name"));
            if (rs.getString("client_name") != null) {
                person.setClient((new ClientRowMapper()).mapRow(rs, rowNum));
            } else {
                person.setClient(null);
            }
            return person;
        }
    }
}
