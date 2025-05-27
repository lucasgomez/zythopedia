import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {Router} from "@angular/router";
import {LoginService} from "../../../shared/services/login.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {

  loginForm!: FormGroup;
  userMessage: string = '';

  constructor(
      private fb: FormBuilder,
      private loginService: LoginService,
      private router: Router) {}

  ngOnInit(): void {
    // Initialiser le formulaire
    this.initForm();

    // Vérifier si un utilisateur est connecté et afficher un message adapté
    const loggedUser = this.loginService.getLoggedAs();
    if (loggedUser) {
      this.userMessage = `C'est bien toi qu'on appelle le ${loggedUser}`;
    } else {
      this.userMessage = "Hé l'étranger, si tu veux être ici, tu ferais mieux de te présenter...";
    }
  }

  initForm(): void {
    this.loginForm = this.fb.group({
      login: ['', [Validators.required]],
      password: ['', [Validators.required]],
    });
  }

  onLogin(): void {
    if (this.loginForm.valid) {
      const { login, password } = this.loginForm.value;
      this.loginService.saveLogin(login, password);
      alert(`Ainsi donc, c'est toi le sac à binch qu'on nomme ${login}.`);
      this.router.navigate(['/']);
    } else {
      alert('Veuillez remplir tous les champs correctement.');
    }
  }

  onLogout(): void {
    // Supprime les informations d'utilisateur et remet le message par défaut
    this.loginService.logout();
    alert('Ton nom est personne.');
    this.router.navigate(['/']);
  }
}