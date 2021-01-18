import org.kohsuke.github.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBoard {
    public static void main(String[] args) throws IOException {
        // token 가져오기
        Token secretKey = new Token();
        String token = secretKey.getToken();   // Github Token 가져오기

        // repo 연동
        GitHub github = new GitHubBuilder().withOAuthToken(token).build();
        GHRepository repository = github.getRepository("youngjinmo/live-study").getParent();   // Repository 가져오기

        // Issue 가져오기
        List<GHIssue> issues = repository.getIssues(GHIssueState.ALL);   // 이슈 상태(open/close)와 상관없이 모두 가져오기
        int issueCount = issues.size();   // 이슈 갯수 가져오기

        // 참여자 정보(이름, 참여횟수) 담을 Map 변수 선언
        Map<String, Integer> members = new HashMap<>();

        // 이슈 댓글 가져오기
        for (int i = 1; i <= issueCount; i++) {
            GHIssue issue = repository.getIssue(i);    // Github Issue 가져오기
            int commentCnt = issue.getComments().size();  // 각 이슈별 댓글 수 가져오기

            // 각 이슈에서 iterator 돌려서 댓글에서 이름 추출
            for (int j = 0; j < commentCnt; j++) {
                GHIssueComment comment = issue.getComments().get(j);   // Issue 댓글 가져오기
                String memberName = comment.getUser().getName();   // 댓글 작성자의 이름 가져오기
                int participantCnt=1;   // 참여횟수 초기화

                if(members.containsKey(memberName)){  // 참여이력있는 멤버일경우,
                    participantCnt =  members.get(memberName);  // 컬렉션에서 참여횟수 가져오기
                    members.put(memberName, ++participantCnt);
                } else {
                    members.put(memberName, ++participantCnt);
                }

            }
        }

        // 멤버이름과 참여율 출력하기
        for (String name : members.keySet()) {
            double participants = Double.valueOf(members.get(name));  // 참여횟수의 타입을 double로 캐스팅
            double rate = participants/issueCount;   // 참여율 계산
            System.out.printf("%s : %10.2f\n",name,rate);
        }

    }
}
