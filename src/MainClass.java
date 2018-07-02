import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainClass {
	
	//Input : src/file/Input.txt
	//Ouput : src/file/Output.txt
	
	public static void main(String[] args) {
		
		ArrayList<Product> productList = new ArrayList<Product>();
		Map<String,List<ProductSurvey>> competitorListByProductName = new HashMap<>();
		
		ClassLoader classLoader = new MainClass().getClass().getClassLoader();
		File file = new File(classLoader.getResource("file/Input.txt").getFile());
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

			getProducts(productList, br);
			readsurvey(competitorListByProductName, br);
			filterAndSort(competitorListByProductName);
			PrintsellingPrice(productList, competitorListByProductName);
			
			
            
		} catch (Exception e) {
		  System.out.println(e.getMessage());
		}
	}

	private static void PrintsellingPrice(ArrayList<Product> productList,
			Map<String, List<ProductSurvey>> competitorListByProductName) {
		
		ClassLoader classLoader = new MainClass().getClass().getClassLoader();
		File file = new File(classLoader.getResource("file/Output.txt").getFile());
		char ch = 'A';
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath().replace("bin", "src")))) {
			
			for (Product product : productList) {
				
				List<ProductSurvey> productSurveyList=competitorListByProductName.get(product.getProductName());
				Float price =((ProductSurvey)productSurveyList.get(0)).getProductPrice();
				Float finalPrice = 0.0f;
				if("H".equals(product.getDemandCode())) {
					
					if("H".equals(product.getSupplyCode())) {
						finalPrice = price;
						
					} else {
						finalPrice = price+(price*0.05f);
					}
					
				} else if("L".equals(product.getDemandCode())) {
					

					if("H".equals(product.getSupplyCode())) {
						finalPrice = price-(price*0.05f);
					} else {
						finalPrice = price+(price*0.1f);
					}
					
				}
				
				bw.write(ch+" "+finalPrice);
				bw.newLine();
				ch++;
			}
			//bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void filterAndSort(Map<String, List<ProductSurvey>> competitorListByProductName) {
		for(String productName : competitorListByProductName.keySet()) {
			
			List<ProductSurvey> productSurveyList =competitorListByProductName.get(productName);
			Float avgProductPrice = 0.0f;
			Float totalProductPrice = 0.0f;
			int noOfProducts = 0;
			for (ProductSurvey productSurvey : productSurveyList) {
				totalProductPrice+=productSurvey.getProductPrice()*productSurvey.getCompetitorList().size();
				noOfProducts+=productSurvey.getCompetitorList().size();
			}
			
			
			if(noOfProducts>=0) {
			avgProductPrice = totalProductPrice/noOfProducts;
			
			List<ProductSurvey> filterProductSurveyList = new ArrayList<>();
			for(ProductSurvey p : productSurveyList) {
				if((p.getProductPrice().floatValue() <= avgProductPrice*1.5f) &&
						   (p.getProductPrice().floatValue() >= avgProductPrice*0.5f)) {
					filterProductSurveyList.add(p);
				}
			}
			Collections.sort(filterProductSurveyList,new ProductSurveyComparator());
			competitorListByProductName.put(productName,filterProductSurveyList);
			}
			
		}
		
	}

	private static void readsurvey(Map<String, List<ProductSurvey>> competitorListByProductName, BufferedReader br)
			throws IOException {
		Integer  noOfSurveys = 0;
		
		
		
		Pattern p = Pattern.compile( "([0-9]+)" );
		Pattern productInfoMather = Pattern.compile( "([0-9,A-Z,a-z]+ [0-9,A-Z,a-z]+ [0-9]+.*[0-9]*)" );
		
		
		String noOfSurveysLine =br.readLine();
		
		if(null == noOfSurveysLine) {
			throw new RuntimeException("Invalid Input");
		}
		
        Matcher m = p.matcher(noOfSurveysLine);
		if(!m.matches()) {
			throw new RuntimeException("Invalid Input");
		}
		
		noOfSurveys = Integer.valueOf(noOfSurveysLine);
		while(noOfSurveys>0) {
			String productCompetitor = br.readLine();
			
			if(null == productCompetitor) {
				throw new RuntimeException("Invalid Input");
			}
			 m = productInfoMather.matcher(productCompetitor);
				if(!m.matches()) {
					throw new RuntimeException("Invalid Input");
				}
			
			String[] productCompetitorArray = productCompetitor.split(" ");
			
			if(competitorListByProductName.containsKey(productCompetitorArray[0])) {
				
				List<ProductSurvey> productSurveyList =competitorListByProductName.get(productCompetitorArray[0]);
				Optional<ProductSurvey> productSurveyInfo = productSurveyList.stream().filter(productSurvey ->
				     productSurvey.getProductPrice().floatValue() == Float.valueOf(productCompetitorArray[2])
				     ).findAny();
				if(productSurveyInfo.isPresent()) {
					productSurveyInfo.get().getCompetitorList().add(productCompetitorArray[1]);
				} else {
					ProductSurvey productSurvey = new ProductSurvey();
					productSurvey.setProductName(productCompetitorArray[0]);
					productSurvey.getCompetitorList().add(productCompetitorArray[1]);
					productSurvey.setProductPrice(Float.valueOf(productCompetitorArray[2]));
					productSurveyList.add(productSurvey);
				}
				
				
			} else {
				
				List<ProductSurvey> productSurveyList = new ArrayList<ProductSurvey>();
				ProductSurvey productSurvey = new ProductSurvey();
				productSurvey.setProductName(productCompetitorArray[0]);
				productSurvey.getCompetitorList().add(productCompetitorArray[1]);
				productSurvey.setProductPrice(Float.valueOf(productCompetitorArray[2]));
				productSurveyList.add(productSurvey);
				competitorListByProductName.put(productCompetitorArray[0], productSurveyList);
				
			}
			noOfSurveys--;
		}
	}

	private static void getProducts(ArrayList<Product> productList, BufferedReader br) throws IOException {
		Integer  noOfProducts = 0;
		
		Pattern p = Pattern.compile( "([0-9]+)" );
		Pattern productInfoMather = Pattern.compile( "([0-9,A-Z,a-z]+ [A-Z] [A-Z])" );
		
		
		String noOfProductsline =br.readLine();
		
		if(null == noOfProductsline) {
			throw new RuntimeException("Invalid Input");
		}
		
        Matcher m = p.matcher(noOfProductsline);
		if(!m.matches()) {
			throw new RuntimeException("Invalid Input");
		}
		
		noOfProducts = Integer.valueOf(noOfProductsline);
		while(noOfProducts>0) {
			String productInfo = br.readLine();
			
			if(null == productInfo) {
				throw new RuntimeException("Invalid Input");
			}
			
			m = productInfoMather.matcher(productInfo);
	        if(!m.matches()) {
				throw new RuntimeException("Invalid Input");
			}
			
			String[] productInfoArray = productInfo.split(" ");
			Product product =  new Product();
			product.setProductName(productInfoArray[0]);
			product.setSupplyCode(productInfoArray[1]);
			product.setDemandCode(productInfoArray[2]);
			productList.add(product);
			noOfProducts--;
		}
	}

}
