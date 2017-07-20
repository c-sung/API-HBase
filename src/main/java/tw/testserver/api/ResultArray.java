package tw.testserver.api;

import java.util.ArrayList;

public class ResultArray {
    private ArrayList<Member> member = new ArrayList<>();
    private String ans;

    public ArrayList<Member> getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member.add(member);
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

}
