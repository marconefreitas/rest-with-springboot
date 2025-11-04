package br.com.marconefreitas.integrationtests.wrappers.xml;

import br.com.marconefreitas.integrationtests.dto.PersonDTO;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.List;

@XmlRootElement
public class PagedModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement
    private List<PersonDTO> content;

    public PagedModel() {
    }

    public List<PersonDTO> getContent() {
        return content;
    }

    public void setContent(List<PersonDTO> content) {
        this.content = content;
    }
}
