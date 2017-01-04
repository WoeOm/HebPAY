package heb.pay.entity;  

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.relation.Role;


public class User implements Serializable{  
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7125130870110613224L;
	private String userid;
	private String usercode;//
	private String username;
	private String userpwd;	
	private String email;
	private String mb;	
	private String usertype;
	private Date registerdate;
	private String userpid;
	private List<Role> roles = new ArrayList<Role>();
	private Date activatedate;
	private String activationcode;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activatedate == null) ? 0 : activatedate.hashCode());
		result = prime * result + ((activationcode == null) ? 0 : activationcode.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((mb == null) ? 0 : mb.hashCode());
		result = prime * result + ((registerdate == null) ? 0 : registerdate.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + ((usercode == null) ? 0 : usercode.hashCode());
		result = prime * result + ((userid == null) ? 0 : userid.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		result = prime * result + ((userpid == null) ? 0 : userpid.hashCode());
		result = prime * result + ((userpwd == null) ? 0 : userpwd.hashCode());
		result = prime * result + ((usertype == null) ? 0 : usertype.hashCode());
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
		if (activatedate == null) {
			if (other.activatedate != null)
				return false;
		} else if (!activatedate.equals(other.activatedate))
			return false;
		if (activationcode == null) {
			if (other.activationcode != null)
				return false;
		} else if (!activationcode.equals(other.activationcode))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (mb == null) {
			if (other.mb != null)
				return false;
		} else if (!mb.equals(other.mb))
			return false;
		if (registerdate == null) {
			if (other.registerdate != null)
				return false;
		} else if (!registerdate.equals(other.registerdate))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		if (usercode == null) {
			if (other.usercode != null)
				return false;
		} else if (!usercode.equals(other.usercode))
			return false;
		if (userid == null) {
			if (other.userid != null)
				return false;
		} else if (!userid.equals(other.userid))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (userpid == null) {
			if (other.userpid != null)
				return false;
		} else if (!userpid.equals(other.userpid))
			return false;
		if (userpwd == null) {
			if (other.userpwd != null)
				return false;
		} else if (!userpwd.equals(other.userpwd))
			return false;
		if (usertype == null) {
			if (other.usertype != null)
				return false;
		} else if (!usertype.equals(other.usertype))
			return false;
		return true;
	}

	public String getUserid() {
		return userid;
	}





	public void setUserid(String userid) {
		this.userid = userid;
	}





	public String getUsercode() {
		return usercode;
	}





	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}





	public String getUsername() {
		return username;
	}





	public void setUsername(String username) {
		this.username = username;
	}





	public String getUserpwd() {
		return userpwd;
	}

	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMb() {
		return mb;
	}

	public void setMb(String mb) {
		this.mb = mb;
	}





	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public Date getRegisterdate() {
		return registerdate;
	}

	public void setRegisterdate(Date registerdate) {
		this.registerdate = registerdate;
	}

	public String getUserpid() {
		return userpid;
	}

	public void setUserpid(String userpid) {
		this.userpid = userpid;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Date getActivatedate() {
		return activatedate;
	}

	public void setActivatedate(Date activatedate) {
		this.activatedate = activatedate;
	}

	public String getActivationcode() {
		return activationcode;
	}

	public void setActivationcode(String activationcode) {
		this.activationcode = activationcode;
	}

	public String getRoleName(User user){  
        String ret = "";
		if(user.getUsertype().equals("0")){
        	ret = "TEMP";
        }else if(user.getUsertype().equals("1") || user.getUsertype().equals("2")){
        	ret = "USER";
        }else if(user.getUsertype().equals("3")){
        	ret = "USER";
        }else if(user.getUsertype().equals("4")){
        	ret = "MAINTAIN";
        }else if(user.getUsertype().equals("5")){
        	ret = "ADMIN";
        }else{
        	ret = "NONE";
        }
        return ret;  
    } 


} 