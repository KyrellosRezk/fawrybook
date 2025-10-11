import { Component, OnInit } from '@angular/core';
import { UserBasicData } from '../../auth/payloads/responses/user-basic-data';
import { HttpClient } from '@angular/common/http';
import { Route, Router } from '@angular/router';
import { Auth } from '../../auth/auth';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit{
  user?: UserBasicData;
  postsPage: Page<UserBasicData> = { content: [], page: 0, size: 10, totalElements: 0, totalPages: 0 };
  suggestionsPage: Page<SuggestionFriend> = { content: [], page: 0, size: 5, totalElements: 0, totalPages: 0 };

  constructor(
    private auth: Auth,
    private home: Home,
    private http: HttpClient,
    private router: Router
  ) {}
}
