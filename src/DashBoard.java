import org.kohsuke.github.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBoard {
    public static void main(String[] args) throws IOException {

        final String repoName = "youngjinmo/live-study";

        // Token 가져와서 Repository를 만들어서 반환
        GHRepository repository = getRepository(repoName);

        // 이슈 List 형태로 가져오기
        List<GHIssue> issues = getIssues(repository);

        // 이슈 댓글에서 참여자 정보(이름, 참여횟수) 가져오기
        Map<String, Integer> members = getParticipants(issues);

        // 멤버이름과 참여율 출력하기
        printOutParticipantsRate(members, issues.size());

    }

    /**
     *  Token 가져와서 Repository를 만들어서 반환
     *
     * @param repoName
     * @return
     * @throws IOException
     */
    private static GHRepository getRepository(String repoName) throws IOException {
        Token githubToken = new Token();
        String token = githubToken.getToken();   // token 가져오기

        GitHub github = new GitHubBuilder().withOAuthToken(token).build();   // token으로 github 빌드
        return github.getRepository(repoName).getParent();
    }

    /**
     *  이슈 List 형태로 가져오기
     *
     * @param repository
     * @return
     * @throws IOException
     */
    private static List<GHIssue> getIssues(GHRepository repository) throws IOException {
        return repository.getIssues(GHIssueState.ALL);
    }

    /**
     * 이슈의 댓글에서 참여자 이름과 참여횟수 가져오기
     *
     * @param issues
     * @return
     * @throws IOException
     */
    private static Map<String, Integer> getParticipants(List<GHIssue> issues) throws IOException {

        // 참여자 이름과 참석수 담을 컬렉션 선언
        Map<String, Integer> members = new HashMap<>();

        // Repository의 Issue들을 iterator 돌려서 개별 이슈 가져오기
        for (int i = issues.size()-1; i >= 0; i--) {
            GHIssue issue = issues.get(i);   // 인덱스로 개별 이슈 가져오기
            int participant = 1;
            for (int j = 0; j < issue.getComments().size(); j++) {
                GHIssueComment comment = issue.getComments().get(j);
                String memberName = comment.getUser().getName();
                if(members.containsKey(memberName)){
                    participant = members.get(memberName)+1;
                    members.put(memberName,participant);
                } else {
                    members.put(memberName,participant);
                }
            }
        }
        return members;
    }

    /**
     *  멤버 이름과 참여율 출력
     *
     * @param members
     * @param countAllIssues
     */
    private static void printOutParticipantsRate(Map<String, Integer> members, int countAllIssues){
        for (String name : members.keySet()) {
            double rate = (double)members.get(name)/(double)countAllIssues;   // 참여율 계산
            System.out.printf("\n%-20s : %10.2f%s",name,rate,"%");
        }
    }
}
