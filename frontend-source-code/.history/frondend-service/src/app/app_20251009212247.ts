import { Component } from "@angular/core";


@Component({
  selector: 'app-root',
  imports: [RouterOutle],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frondend-service');
}
