package kr.co.tbell.echeck.model;

public class Agree {

    private Long id;

    private String agreeName;

    private String agreeType;

    private String agreeYn;

    private String createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAgreeName() {
        return agreeName;
    }

    public void setAgreeName(String agreeName) {
        this.agreeName = agreeName;
    }

    public String getAgreeType() {
        return agreeType;
    }

    public void setAgreeType(String agreeType) {
        this.agreeType = agreeType;
    }

    public String getAgreeYn() {
        return agreeYn;
    }

    public void setAgreeYn(String agreeYn) {
        this.agreeYn = agreeYn;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Agree{" +
                "id=" + id +
                ", agreeName='" + agreeName + '\'' +
                ", agreeType='" + agreeType + '\'' +
                ", agreeYn='" + agreeYn + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
