package br.com.marconefreitas.integrationtests.wrappers.json;

import br.com.marconefreitas.integrationtests.dto.PersonDTO;

import java.io.Serializable;
import java.util.List;

public class PersonEmbeddedDTO implements Serializable {

    private List<PersonDTO> people;

    public PersonEmbeddedDTO() {
    }

    public List<PersonDTO> getPeople() {
        return people;
    }

    public void setPeople(List<PersonDTO> people) {
        this.people = people;
    }
}
