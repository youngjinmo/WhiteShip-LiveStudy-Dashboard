import org.kohsuke.github.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBoardTest {
    public static void main(String[] args) throws IOException {

        // token 가져오기
        Token key = new Token();
        String token = key.getToken();   // token 가져오기
        GitHub github = new GitHubBuilder().withOAuthToken(token).build();   // token으로 github 빌드
        GHRepository repository =  github.getRepository("youngjinmo/live-study").getParent();

        // issue 가져오기
        List<GHIssue> issues = repository.getIssues(GHIssueState.ALL);
        int issueCount =  issues.size();

        Map<String, Integer> members = new HashMap<>();

        // 개별 이슈에서 댓글 가져오기
        for (int i=issueCount-1; i>=12; i--) {
            GHIssue issue = issues.get(i);
            int commentCnt = issue.getComments().size();  // 각 이슈별 댓글 수 가져오기

            // 이슈 가져오기
            System.out.println(issue.getTitle());
            System.out.println("댓글 갯수 : "+issue.getComments().size());

            // 각 이슈에서 iterator 돌려서 댓글에서 이름 추출
            for (int j = 0; j < 5; j++) {
                GHIssueComment comment = issue.getComments().get(j);   // Issue 댓글 가져오기
                String memberName = comment.getUser().getName();   // 댓글 작성자의 이름 가져오기
                int participantCnt=1;   // 참여횟수 초기화

                if(members.containsKey(memberName)){  // 참여이력있는 멤버일경우,
                    participantCnt =  members.get(memberName);  // 컬렉션에서 참여횟수 가져오기
                    members.put(memberName, participantCnt+1);
                } else {
                    members.put(memberName, participantCnt);
                }
            }
        }

        // 참여자 과제 제출률 출력
        for (String name : members.keySet()) {
            double rate = (double)members.get(name)/(double)issueCount;   // 참여율 계산
            System.out.printf("\n%-20s : %10.2f%s",name,rate,"%");
        }
    }
}
