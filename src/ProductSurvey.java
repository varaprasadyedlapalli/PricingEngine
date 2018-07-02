import java.util.ArrayList;
import java.util.List;

public class ProductSurvey {

	String productName;
	Float productPrice;
	List<String> competitorList = new ArrayList<String>();
	
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Float getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(Float productPrice) {
		this.productPrice = productPrice;
	}
	public List<String> getCompetitorList() {
		return competitorList;
	}
	public void setCompetitorList(List<String> competitorList) {
		this.competitorList = competitorList;
	}
	
	
	
}
