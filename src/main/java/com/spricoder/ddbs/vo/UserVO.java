/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.spricoder.ddbs.vo;

import com.spricoder.ddbs.data.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

  private String uid;
  private String name;
  private String gender;
  private String email;
  private String phone;
  private String dept;
  private String grade;
  private String language;
  private String region;
  private String role;
  private String preferTags;
  private Integer obtainedCredits;

  public UserVO(User user) {
    this.uid = user.getUid();
    this.name = user.getName();
    this.gender = user.getGender();
    this.email = user.getEmail();
    this.phone = user.getPhone();
    this.dept = user.getDept();
    this.grade = user.getGrade();
    this.language = user.getLanguage();
    this.region = user.getRegion();
    this.role = user.getRole();
    this.preferTags = user.getPreferTags();
    this.obtainedCredits = user.getObtainedCredits();
  }
}
