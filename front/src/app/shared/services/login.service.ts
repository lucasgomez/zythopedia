import { Injectable } from '@angular/core';
import {Router} from "@angular/router";
import {HttpHeaders} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private readonly router: Router) { }

  USERNAME_PARAM_NAME = 'username';
  PASSWORD_PARAM_NAME = 'password';

  saveLogin(username: string, password: string) {
    //TODO cypher data, but local storage, so for now, balek 🍒🫲
    localStorage.setItem(this.USERNAME_PARAM_NAME, username);
    localStorage.setItem(this.PASSWORD_PARAM_NAME, password);
  }

  isLoggedIn(): boolean {
    return localStorage.getItem(this.USERNAME_PARAM_NAME) != null;
  }

  getLoggedAs(): string | null {
    return localStorage.getItem(this.USERNAME_PARAM_NAME);
  }

  logout(): void {
    localStorage.removeItem(this.USERNAME_PARAM_NAME);
    localStorage.removeItem(this.PASSWORD_PARAM_NAME);
  }

  getHttpOptions(): {headers: HttpHeaders} {
    if (this.isLoggedIn()) {
      return {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Authorization': `Basic ${btoa(`${localStorage.getItem(this.USERNAME_PARAM_NAME)}:${localStorage.getItem(this.PASSWORD_PARAM_NAME)}`)}`
        })
      };
    }
    alert("Il faut être loggé pour accéder à cette page");
    this.router.navigate(['/drinks/login']);
    throw new Error("Not logged");
  }
}