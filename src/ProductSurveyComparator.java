import java.util.Comparator;

class ProductSurveyComparator implements Comparator<ProductSurvey> {
    @Override
    public int compare(ProductSurvey o1, ProductSurvey o2) {
        //getClass()
       int result = new Integer(o2.getCompetitorList().size()).compareTo(o1.getCompetitorList().size());
        if(result == 0) {
        	result = o1.getProductPrice().compareTo(o2.getProductPrice());
        }
        return result;
    }
}