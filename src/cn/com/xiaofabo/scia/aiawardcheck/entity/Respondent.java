package cn.com.xiaofabo.scia.aiawardcheck.entity;

public class Respondent {
	private String respondentName;
	private String id;
	private String agency;
	private String representative;
	private String address;

	/// Either COM (company) or IND (individual)
	/// this is determined by the id type
	private String type;

	public Respondent() {
    }

	public Respondent(String respondentName, String id, String agency, String representative, String address) {
        this.respondentName = respondentName;
        this.id = id;
        this.agency = agency;
        this.representative = representative;
        this.address = address;
    }

	public String getRespondentName() {
		return respondentName;
	}

	public void setRespondentName(String respondentName) {
		this.respondentName = respondentName;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public String getRepresentative() {
		return representative;
	}

	public void setRepresentative(String representative) {
		this.representative = representative;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
