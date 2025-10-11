import { Component, OnInit } from '@angular/core';
import { UserBasicData } from '../../auth/payloads/responses/user-basic-data';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit{
  user?: UserBasicData;
  
}
