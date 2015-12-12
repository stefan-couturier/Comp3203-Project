package p1;

public class FileRequest {
	public enum Status { WAITING, PENDING, VERIFYING, COMPLETED };
	private String description;
	private String posterIP;
	private String poster;
	private String responder;
	private Status status;
	
	FileRequest(String d, String pIP, String p) {
		description = d;
		posterIP = pIP;
		poster = p;
		responder = "";
		status = Status.WAITING;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getPosterIP() {
		return posterIP;
	}

	public void setPosterIP(String posterIP) {
		this.posterIP = posterIP;
	}

	public String getResponder() {
		return responder;
	}

	public void setResponder(String responder) {
		this.responder = responder;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	
}
