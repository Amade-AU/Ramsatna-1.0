package amada.ramsatna.model.configs;
import com.google.gson.annotations.SerializedName;

/**
 * Model Class for Params object from JSON
 */
public class Params {

        @SerializedName("3")
        private String three;

        @SerializedName("2")
        private String two;

        @SerializedName("1")
        private String one;

        private String news_2_url;

        @SerializedName("0")
        private String zero;

        @SerializedName("5")
        private String five;

        private String news_2_image_link;

        @SerializedName("4")
        private String four;

        private String news_2_title;


        private String news_1_title;

        private String news_1_image_link;

        private String news_1_url;

        public String getNews_1_url() {
                return news_1_url;
        }

        public void setNews_1_url(String news_1_url) {
                this.news_1_url = news_1_url;
        }

        public String getTwo() {
                return two;
        }

        public void setTwo(String two) {
                this.two = two;
        }

        public String getOne() {
                return one;
        }

        public void setOne(String one) {
                this.one = one;
        }

        public String getNews_2_url() {
                return news_2_url;
        }

        public void setNews_2_url(String news_2_url) {
                this.news_2_url = news_2_url;
        }

        public String getZero() {
                return zero;
        }

        public void setZero(String zero) {
                this.zero = zero;
        }

        public String getFive() {
                return five;
        }

        public void setFive(String five) {
                this.five = five;
        }

        public String getNews_2_image_link() {
                return news_2_image_link;
        }

        public void setNews_2_image_link(String news_2_image_link) {
                this.news_2_image_link = news_2_image_link;
        }

        public String getFour() {
                return four;
        }

        public void setFour(String four) {
                this.four = four;
        }

        public String getNews_2_title() {
                return news_2_title;
        }

        public void setNews_2_title(String news_2_title) {
                this.news_2_title = news_2_title;
        }

        public String getNews_1_title() {
                return news_1_title;
        }

        public void setNews_1_title(String news_1_title) {
                this.news_1_title = news_1_title;
        }

        public String getNews_1_image_link() {
                return news_1_image_link;
        }

        public void setNews_1_image_link(String news_1_image_link) {
                this.news_1_image_link = news_1_image_link;
        }

        public String getThree() {
                return three;
        }

        public void setThree(String three) {
                this.three = three;
        }

        @Override
        public String toString()
        {
                return "ClassPojo [3 = "+3+", 2 = "+2+", 1 = "+1+", news_2_url = "+news_2_url+", 0 = "+0+", 5 = "+5+", news_2_image_link = "+news_2_image_link+", 4 = "+4+", news_2_title = "+news_2_title+", news_1_title = "+news_1_title+", news_1_image_link = "+news_1_image_link+", news_1_url = "+news_1_url+"]";
        }

}
