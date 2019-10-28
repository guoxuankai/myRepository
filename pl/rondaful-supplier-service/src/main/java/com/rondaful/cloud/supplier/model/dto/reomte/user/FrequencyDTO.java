package com.rondaful.cloud.supplier.model.dto.reomte.user;

import java.io.Serializable;

public class FrequencyDTO implements Serializable {
	
	 private static final long serialVersionUID = -2355172539056831039L;
	 
		private Integer frequencyAstrict;

		private String routeURL;


		public Integer getFrequencyAstrict() {
			return frequencyAstrict;
		}


		public void setFrequencyAstrict(Integer frequencyAstrict) {
			this.frequencyAstrict = frequencyAstrict;
		}


		public String getRouteURL() {
			return routeURL;
		}


		public void setRouteURL(String routeURL) {
			this.routeURL = routeURL;
		}
}
