package etn.app.danghoc.shoppingclient.Model;


import java.util.ArrayList;
import java.util.List;

public class TestModelCha {
   Boolean success;
   List<TestModel>result;

   public TestModelCha(Boolean success, List<TestModel> result) {
      this.success = success;
      this.result = result;
   }

   public Boolean getSuccess() {
      return success;
   }

   public void setSuccess(Boolean success) {
      this.success = success;
   }

   public List<TestModel> getResult() {
      return result;
   }

   public void setResult(List<TestModel> result) {
      this.result = result;
   }
}
