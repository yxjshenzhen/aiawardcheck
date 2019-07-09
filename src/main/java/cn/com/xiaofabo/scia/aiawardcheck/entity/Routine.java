package cn.com.xiaofabo.scia.aiawardcheck.entity;


import java.util.List;

public class Routine {
	private final String awardDate;
	private final String routineText;

	private final List proposerList;
	private final List respondentList;

	public Routine(List proposerList, List respondentList, String awardDate, String routineText) {
		this.proposerList = proposerList;
		this.respondentList = respondentList;
		this.awardDate = awardDate;
		this.routineText = routineText;
	}

	public String getAwardDate() {
		return awardDate;
	}

	public String getRoutineText() {
		return routineText;
	}

	public List getProposerList() {
		return proposerList;
	}

	public List getRespondentList() {
		return respondentList;
	}
}
