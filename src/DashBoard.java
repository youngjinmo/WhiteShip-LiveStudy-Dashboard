import org.kohsuke.github.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBoard {
    public static void main(String[] args) throws IOException {

        final String repo = "youngjinmo/live-study";

        // repo 연동
        GHRepository repository = getRepository(repo);

        // 전체 이슈 갯수 가져오기
        int countAllIssues = getCountParticipant(repository);

        // 참여자 정보(이름, 참여횟수) 가져오기
        Map<String, Integer> members = getParticipants(repository, countAllIssues);

        // 멤버이름과 참여율 출력하기
        printOutParticipantsRate(members, countAllIssues);

    }

    /**
     *  Token 가져와서 Repository를 만들어서 반환
     *
     * @param repo
     * @return
     * @throws IOException
     */
    private static GHRepository getRepository(String repo) throws IOException {
        Token githubToken = new Token();
        String token = githubToken.getToken();   // token 가져오기

        GitHub github = new GitHubBuilder().withOAuthToken(token).build();   // token으로 github 빌드
        return github.getRepository(repo).getParent();
    }

    /**
     *  Repository의 이슈 갯수 가져오기
     *
     * @param repository
     * @return
     * @throws IOException
     */
    private static int getCountParticipant(GHRepository repository) throws IOException {
        // 이슈 상태(open/close)와 상관없이 모두 가져오기
        List<GHIssue> issues = repository.getIssues(GHIssueState.ALL);
        return issues.size();
    }

    /**
     *  이슈의 댓글에서 참여자 이름과 참여횟수 가져오기
     *
     * @param repository
     * @param countAllIssues
     * @return
     * @throws IOException
     */
    private static Map<String, Integer> getParticipants(GHRepository repository, int countAllIssues) throws IOException {
        Map<String, Integer> members = new HashMap<>();

        // Repository의 Issue들을 iterator 돌려서 개별 이슈 가져오기
        for (int i = countAllIssues-1; i>=0; i--) {
            GHIssue issue = repository.getIssue(i);   // issue 가져오기
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
                    members.put(memberName, participantCnt);
                }
            }
        }
        return members;
    }

    /**
     *  멤버 이름과 참여율 출력
     *
     * @param data
     * @param countAllIssues
     */
    private static void printOutParticipantsRate(Map<String, Integer> data, int countAllIssues){
        for (String name : data.keySet()) {
            double rate = (double)data.get(name)/(double)countAllIssues;   // 참여율 계산
            System.out.printf("\n%-20s : %10.2f%s",name,rate,"%");
        }
    }
}
