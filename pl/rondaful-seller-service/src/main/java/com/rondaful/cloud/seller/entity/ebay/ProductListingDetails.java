package com.rondaful.cloud.seller.entity.ebay;



public class ProductListingDetails {
	private BrandMPN brandMPN;
	private TicketListingDetails ticketListingDetails;
	private String EAN;
	private boolean includeeBayProductDetails;
	private boolean includeStockPhotoURL;
	private String ISBN;
	private String productReferenceID;
	private boolean returnSearchResultOnDuplicates;
	private String UPC;
	private boolean useFirstProduct;
	private boolean useStockPhotoURLAsGallery;
	public BrandMPN getBrandMPN() {
		return brandMPN;
	}
	public void setBrandMPN(BrandMPN brandMPN) {
		this.brandMPN = brandMPN;
	}
	public TicketListingDetails getTicketListingDetails() {
		return ticketListingDetails;
	}
	public void setTicketListingDetails(TicketListingDetails ticketListingDetails) {
		this.ticketListingDetails = ticketListingDetails;
	}
	public String getEAN() {
		return EAN;
	}
	public void setEAN(String eAN) {
		EAN = eAN;
	}
	public boolean isIncludeeBayProductDetails() {
		return includeeBayProductDetails;
	}
	public void setIncludeeBayProductDetails(boolean includeeBayProductDetails) {
		this.includeeBayProductDetails = includeeBayProductDetails;
	}
	public boolean isIncludeStockPhotoURL() {
		return includeStockPhotoURL;
	}
	public void setIncludeStockPhotoURL(boolean includeStockPhotoURL) {
		this.includeStockPhotoURL = includeStockPhotoURL;
	}
	public String getISBN() {
		return ISBN;
	}
	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}
	public String getProductReferenceID() {
		return productReferenceID;
	}
	public void setProductReferenceID(String productReferenceID) {
		this.productReferenceID = productReferenceID;
	}
	public boolean isReturnSearchResultOnDuplicates() {
		return returnSearchResultOnDuplicates;
	}
	public void setReturnSearchResultOnDuplicates(boolean returnSearchResultOnDuplicates) {
		this.returnSearchResultOnDuplicates = returnSearchResultOnDuplicates;
	}
	public String getUPC() {
		return UPC;
	}
	public void setUPC(String uPC) {
		UPC = uPC;
	}
	public boolean isUseFirstProduct() {
		return useFirstProduct;
	}
	public void setUseFirstProduct(boolean useFirstProduct) {
		this.useFirstProduct = useFirstProduct;
	}
	public boolean isUseStockPhotoURLAsGallery() {
		return useStockPhotoURLAsGallery;
	}
	public void setUseStockPhotoURLAsGallery(boolean useStockPhotoURLAsGallery) {
		this.useStockPhotoURLAsGallery = useStockPhotoURLAsGallery;
	}
}
