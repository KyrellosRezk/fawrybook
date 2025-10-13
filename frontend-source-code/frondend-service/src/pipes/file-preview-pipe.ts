import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filePreview',
  standalone: true
})
export class FilePreviewPipe implements PipeTransform {
  transform(file: File | string): string {
    if (typeof file === 'string') return file;
    return URL.createObjectURL(file);
  }
}