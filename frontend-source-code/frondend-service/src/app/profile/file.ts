import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UploadFilesResponse } from '../payloads/responses/upload-files.response';

@Injectable({
  providedIn: 'root'
})
export class FileService {

  private readonly apiUrl = 'http://localhost:8082/file-management/api/v1/file';

  constructor(private http: HttpClient) {}

  getPathsInFolder(entityType: string, entityId: string, userId: string): Observable<string[]> {
    console.log(entityType);
    let path: string;
    if(entityType == 'PROFILE') {
      path = userId + '/' + entityType;
    } else {
      path = userId + '/' + entityType + '/' + entityId;
    }
    return this.http.get<string[]>(`${this.apiUrl}/files?path=${path}`);
  }

  saveFiles(entityName: string, entityId: string, selectedFiles: File[]): Observable<UploadFilesResponse> {
    const formData = new FormData();

    selectedFiles.forEach((file, index) => {
      const ext = file.name.split('.').pop();
      const key = `${index + 1}.${ext}`;
      formData.append(`files[${key}]`, file);
    });

    formData.append('entity_name', entityName);
    formData.append('entity_id', entityId);

    return this.http.post<UploadFilesResponse>(this.apiUrl, formData);
  }

  saveProfileImage(profileImage: File): Observable<UploadFilesResponse> {
    const formData = new FormData();
    formData.append('file', profileImage);

    const otpToken = localStorage.getItem('otpToken');

    const headers = new HttpHeaders({
      Authorization: `Bearer ${otpToken}`
    });

    return this.http.post<UploadFilesResponse>(
      `${this.apiUrl}/profile`,
      formData,
      { headers }
    );
  }

  getFile(path: string): Observable<Blob> {
    const url = `${this.apiUrl}?path=${encodeURIComponent(path)}`;
    return this.http.get(url, { responseType: 'blob' });
  }
}