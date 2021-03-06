package com.aquent.crudapp.dao.client;

import com.aquent.crudapp.dto.Client;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Operations on the "client" table.
 */
@Repository
public interface ClientDao {

    /**
     * Retrieves all of the client records.
     *
     * @return list of client records
     */
    List<Client> listClients();

    /**
     * Creates a new client record.
     *
     * @param client the values to save
     * @return the new client ID
     */
    Integer createClient(Client client);

    /**
     * Retrieves a client record by ID.
     *
     * @param id the client ID
     * @return the client record
     */
    Client readClient(Integer id);

    /**
     * Retrieves a client record by name.
     *
     * @param clientName the client name
     * @return the client record
     */
    Client readClient(String clientName);

    /**
     * Updates an existing client record.
     *
     * @param client the new values to save
     */
    void updateClient(Client client);

    /**
     * Deletes a client record by ID.
     *
     * @param id the client ID
     */
    void deleteClient(Integer id);
}
