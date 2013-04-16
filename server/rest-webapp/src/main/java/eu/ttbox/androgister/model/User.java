package eu.ttbox.androgister.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

import eu.ttbox.androgister.model.validation.ContraintsUserCreation;

@Entity
@Table(name = "User")
public class User {

//	@Id
	@Column(name = "uuid")
	public UUID uuid;

	@NotEmpty(message = "Login is mandatory.", groups = { ContraintsUserCreation.class, Default.class })
	@NotNull(message = "Login is mandatory.", groups = { ContraintsUserCreation.class, Default.class })
	@Email(message = "Email is invalid.")
	@Id
	@JsonIgnore
	public String login;

	@Column(name = "password")
	@JsonIgnore
	public String password;

	@Column(name = "username")
	public String username;

	@Column(name = "domain")
	@JsonIgnore
	public String domain;

	@Column(name = "avatar")
	public String avatar;

	@Size(min = 0, max = 50)
	@Column(name = "firstName")
	public String firstName;

	@Size(min = 0, max = 50)
	@Column(name = "lastName")
	public String lastName;

	@Size(min = 0, max = 100)
	@Column(name = "jobTitle")
	public String jobTitle;

	@Size(min = 0, max = 20)
	@Column(name = "phoneNumber")
	public String phoneNumber;

	@Column(name = "openIdUrl")
	@JsonIgnore
	public String openIdUrl;

	@Column(name = "theme")
	@JsonIgnore
	public String theme;

	@Column(name = "preferences_mention_email")
	@JsonIgnore
	public Boolean preferencesMentionEmail;

	@Column(name = "rssUid")
	@JsonIgnore
	public String rssUid;

	@Column(name = "weekly_digest_subscription")
	@JsonIgnore
	public Boolean weeklyDigestSubscription;

	@Column(name = "daily_digest_subscription")
	@JsonIgnore
	public Boolean dailyDigestSubscription;

	@Column(name = "attachmentsSize")
	public long attachmentsSize;

	@Column(name = "isNew")
	@JsonIgnore
	public Boolean isNew;

	public long statusCount;

	public long friendsCount;

	public long followersCount;

	@Override
	public String toString() {
		return new StringBuffer(64).append("User [uuid=").append(uuid).append(", version=").append(login) //
				.append(", firstname=").append(firstName)//
				.append(", lastname=").append(lastName) //
				.append("]") //
				.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		return true;
	}
 
	
	 

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getOpenIdUrl() {
		return openIdUrl;
	}

	public void setOpenIdUrl(String openIdUrl) {
		this.openIdUrl = openIdUrl;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public Boolean getPreferencesMentionEmail() {
		return preferencesMentionEmail;
	}

	public void setPreferencesMentionEmail(Boolean preferencesMentionEmail) {
		this.preferencesMentionEmail = preferencesMentionEmail;
	}

	public String getRssUid() {
		return rssUid;
	}

	public void setRssUid(String rssUid) {
		this.rssUid = rssUid;
	}

	public Boolean getWeeklyDigestSubscription() {
		return weeklyDigestSubscription;
	}

	public void setWeeklyDigestSubscription(Boolean weeklyDigestSubscription) {
		this.weeklyDigestSubscription = weeklyDigestSubscription;
	}

	public Boolean getDailyDigestSubscription() {
		return dailyDigestSubscription;
	}

	public void setDailyDigestSubscription(Boolean dailyDigestSubscription) {
		this.dailyDigestSubscription = dailyDigestSubscription;
	}

	public long getAttachmentsSize() {
		return attachmentsSize;
	}

	public void setAttachmentsSize(long attachmentsSize) {
		this.attachmentsSize = attachmentsSize;
	}

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

	public long getStatusCount() {
		return statusCount;
	}

	public void setStatusCount(long statusCount) {
		this.statusCount = statusCount;
	}

	public long getFriendsCount() {
		return friendsCount;
	}

	public void setFriendsCount(long friendsCount) {
		this.friendsCount = friendsCount;
	}

	public long getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(long followersCount) {
		this.followersCount = followersCount;
	}

}
