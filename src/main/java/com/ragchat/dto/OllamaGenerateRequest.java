package com.ragchat.dto;

/**
 * DTO for Ollama generate/chat API request
 */
public class OllamaGenerateRequest {
    
    private String model;
    private String prompt;
    private boolean stream = false;
    private Options options;
    
    public OllamaGenerateRequest() {
    }
    
    public OllamaGenerateRequest(String model, String prompt, boolean stream, Options options) {
        this.model = model;
        this.prompt = prompt;
        this.stream = stream;
        this.options = options;
    }
    
    public static OllamaGenerateRequestBuilder builder() {
        return new OllamaGenerateRequestBuilder();
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getPrompt() {
        return prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    public boolean isStream() {
        return stream;
    }
    
    public void setStream(boolean stream) {
        this.stream = stream;
    }
    
    public Options getOptions() {
        return options;
    }
    
    public void setOptions(Options options) {
        this.options = options;
    }
    
    // Builder class
    public static class OllamaGenerateRequestBuilder {
        private String model;
        private String prompt;
        private boolean stream = false;
        private Options options;
        
        public OllamaGenerateRequestBuilder model(String model) {
            this.model = model;
            return this;
        }
        
        public OllamaGenerateRequestBuilder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }
        
        public OllamaGenerateRequestBuilder stream(boolean stream) {
            this.stream = stream;
            return this;
        }
        
        public OllamaGenerateRequestBuilder options(Options options) {
            this.options = options;
            return this;
        }
        
        public OllamaGenerateRequest build() {
            return new OllamaGenerateRequest(model, prompt, stream, options);
        }
    }
    
    // Options nested class
    public static class Options {
        private Double temperature = 0.7;
        private Integer num_predict = 512;
        private Integer top_k = 40;
        private Double top_p = 0.9;
        
        public Options() {
        }
        
        public Options(Double temperature, Integer num_predict, Integer top_k, Double top_p) {
            this.temperature = temperature;
            this.num_predict = num_predict;
            this.top_k = top_k;
            this.top_p = top_p;
        }
        
        public static OptionsBuilder builder() {
            return new OptionsBuilder();
        }
        
        public Double getTemperature() {
            return temperature;
        }
        
        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }
        
        public Integer getNum_predict() {
            return num_predict;
        }
        
        public void setNum_predict(Integer num_predict) {
            this.num_predict = num_predict;
        }
        
        public Integer getTop_k() {
            return top_k;
        }
        
        public void setTop_k(Integer top_k) {
            this.top_k = top_k;
        }
        
        public Double getTop_p() {
            return top_p;
        }
        
        public void setTop_p(Double top_p) {
            this.top_p = top_p;
        }
        
        // Builder for Options
        public static class OptionsBuilder {
            private Double temperature = 0.7;
            private Integer num_predict = 512;
            private Integer top_k = 40;
            private Double top_p = 0.9;
            
            public OptionsBuilder temperature(Double temperature) {
                this.temperature = temperature;
                return this;
            }
            
            public OptionsBuilder num_predict(Integer num_predict) {
                this.num_predict = num_predict;
                return this;
            }
            
            public OptionsBuilder top_k(Integer top_k) {
                this.top_k = top_k;
                return this;
            }
            
            public OptionsBuilder top_p(Double top_p) {
                this.top_p = top_p;
                return this;
            }
            
            public Options build() {
                return new Options(temperature, num_predict, top_k, top_p);
            }
        }
    }
}
