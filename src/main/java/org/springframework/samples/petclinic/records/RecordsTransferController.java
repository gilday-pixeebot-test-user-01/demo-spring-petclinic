package org.springframework.samples.petclinic.records;

import static io.github.pixee.security.XMLInputFactorySecurity.hardenFactory;
import java.io.InputStream;
import java.util.zip.ZipInputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
class RecordsTransferController {

	@PostMapping("/records-transfers")
	public NewRecordModel newRecordsTransfer(@RequestBody InputStream body) throws java.io.IOException {
		final String id;
		try (var is = new ZipInputStream(body)) {
			id = readRecordId(is);
		}
		return new NewRecordModel(id);
	}

	private String readRecordId(final InputStream is) {
		final var factory = hardenFactory(XMLInputFactory.newFactory());
		try {
			final var xmlEventReader = factory.createXMLEventReader(is);
			while (xmlEventReader.hasNext()) {
				final var xmlEvent = xmlEventReader.nextEvent();
				if (xmlEvent.isStartElement()
						&& "record-id".equals(xmlEvent.asStartElement().getName().getLocalPart())) {
					return xmlEventReader.nextEvent().asCharacters().getData();
				}
			}
		}
		catch (XMLStreamException e) {
			throw new IllegalArgumentException("Invalid XML", e);
		}
		throw new IllegalArgumentException("Invalid XML: no record-id element found");
	}

}
