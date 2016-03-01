#include "ImageProc.h"

METHODDEF(void)
my_error_exit (j_common_ptr cinfo)
{
	my_error_ptr myerr = (my_error_ptr) cinfo->err;
	(*cinfo->err->output_message) (cinfo);
	longjmp(myerr->setjmp_buffer, 1);
}

GLOBAL(void)
jpeg_memory_src (j_decompress_ptr cinfo, void* data, unsigned long len)
{
	memory_src_ptr src;

	if (cinfo->src == NULL) {
	cinfo->src = (struct jpeg_source_mgr *)
	(*cinfo->mem->alloc_small) ((j_common_ptr) cinfo, JPOOL_PERMANENT,
		sizeof(memory_source_mgr));
	}

	src = (memory_src_ptr) cinfo->src;

	src->pub.init_source = memory_init_source;
	src->pub.fill_input_buffer = memory_fill_input_buffer;
	src->pub.skip_input_data = memory_skip_input_data;
	src->pub.resync_to_restart = jpeg_resync_to_restart;
	src->pub.term_source = memory_term_source;
	src->pub.bytes_in_buffer = 0;
	src->pub.next_input_byte = (JOCTET*)data;

	src->skip =0;
}

METHODDEF(void) memory_init_source (j_decompress_ptr cinfo)
{
}


METHODDEF(boolean) memory_fill_input_buffer (j_decompress_ptr cinfo)
{
	return FALSE;
}

METHODDEF(void) memory_skip_input_data (j_decompress_ptr cinfo, long num_bytes)
{
	memory_src_ptr src = (memory_src_ptr) cinfo->src;

	if (num_bytes > (long)src->pub.bytes_in_buffer) {
		src->skip = (int)(num_bytes - src->pub.bytes_in_buffer);
		src->pub.next_input_byte += src->pub.bytes_in_buffer;
		src->pub.bytes_in_buffer = 0;
	}else{
		src->pub.next_input_byte += (size_t) num_bytes;
		src->pub.bytes_in_buffer -= (size_t) num_bytes;
		src->skip=0;
	}
}

METHODDEF(void) memory_term_source (j_decompress_ptr cinfo)
{
}

static
unsigned char my_jpeg_odml_dht[0x1a4] = {
    0xff, 0xc4, 0x01, 0xa2,

    0x00, 0x00, 0x01, 0x05, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00, 0x00,
    0x00, 0x00, 0x00, 0x00, 0x00,
    0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b,

    0x01, 0x00, 0x03, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01,
    0x00, 0x00, 0x00, 0x00, 0x00,
    0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b,

    0x10, 0x00, 0x02, 0x01, 0x03, 0x03, 0x02, 0x04, 0x03, 0x05, 0x05, 0x04,
    0x04, 0x00, 0x00, 0x01, 0x7d,
    0x01, 0x02, 0x03, 0x00, 0x04, 0x11, 0x05, 0x12, 0x21, 0x31, 0x41, 0x06,
    0x13, 0x51, 0x61, 0x07,
    0x22, 0x71, 0x14, 0x32, 0x81, 0x91, 0xa1, 0x08, 0x23, 0x42, 0xb1, 0xc1,
    0x15, 0x52, 0xd1, 0xf0,
    0x24, 0x33, 0x62, 0x72, 0x82, 0x09, 0x0a, 0x16, 0x17, 0x18, 0x19, 0x1a,
    0x25, 0x26, 0x27, 0x28,
    0x29, 0x2a, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x43, 0x44, 0x45,
    0x46, 0x47, 0x48, 0x49,
    0x4a, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x63, 0x64, 0x65,
    0x66, 0x67, 0x68, 0x69,
    0x6a, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a, 0x83, 0x84, 0x85,
    0x86, 0x87, 0x88, 0x89,
    0x8a, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0xa2, 0xa3,
    0xa4, 0xa5, 0xa6, 0xa7,
    0xa8, 0xa9, 0xaa, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba,
    0xc2, 0xc3, 0xc4, 0xc5,
    0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xd2, 0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8,
    0xd9, 0xda, 0xe1, 0xe2,
    0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xf1, 0xf2, 0xf3, 0xf4,
    0xf5, 0xf6, 0xf7, 0xf8,
    0xf9, 0xfa,

    0x11, 0x00, 0x02, 0x01, 0x02, 0x04, 0x04, 0x03, 0x04, 0x07, 0x05, 0x04,
    0x04, 0x00, 0x01, 0x02, 0x77,
    0x00, 0x01, 0x02, 0x03, 0x11, 0x04, 0x05, 0x21, 0x31, 0x06, 0x12, 0x41,
    0x51, 0x07, 0x61, 0x71,
    0x13, 0x22, 0x32, 0x81, 0x08, 0x14, 0x42, 0x91, 0xa1, 0xb1, 0xc1, 0x09,
    0x23, 0x33, 0x52, 0xf0,
    0x15, 0x62, 0x72, 0xd1, 0x0a, 0x16, 0x24, 0x34, 0xe1, 0x25, 0xf1, 0x17,
    0x18, 0x19, 0x1a, 0x26,
    0x27, 0x28, 0x29, 0x2a, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x43, 0x44,
    0x45, 0x46, 0x47, 0x48,
    0x49, 0x4a, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x63, 0x64,
    0x65, 0x66, 0x67, 0x68,
    0x69, 0x6a, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a, 0x82, 0x83,
    0x84, 0x85, 0x86, 0x87,
    0x88, 0x89, 0x8a, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a,
    0xa2, 0xa3, 0xa4, 0xa5,
    0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8,
    0xb9, 0xba, 0xc2, 0xc3,
    0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xd2, 0xd3, 0xd4, 0xd5, 0xd6,
    0xd7, 0xd8, 0xd9, 0xda,
    0xe2, 0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xf2, 0xf3, 0xf4,
    0xf5, 0xf6, 0xf7, 0xf8,
    0xf9, 0xfa
};

static
int my_jpeg_load_dht (struct jpeg_decompress_struct *info, unsigned char *dht,
              JHUFF_TBL *ac_tables[], JHUFF_TBL *dc_tables[])
{
    unsigned int length = (dht[2] << 8) + dht[3] - 2;
    unsigned int pos = 4;
    unsigned int count, i;
    int index;

    JHUFF_TBL **hufftbl;
    unsigned char bits[17];
    unsigned char huffval[256];

    while (length > 16)
    {
       bits[0] = 0;
       index = dht[pos++];
       count = 0;
       for (i = 1; i <= 16; ++i)
       {
           bits[i] = dht[pos++];
           count += bits[i];
       }
       length -= 17;

       if (count > 256 || count > length)
           return -1;

       for (i = 0; i < count; ++i)
           huffval[i] = dht[pos++];
       length -= count;

       if (index & 0x10)
       {
           index -= 0x10;
           hufftbl = &ac_tables[index];
       }
       else
           hufftbl = &dc_tables[index];

       if (index < 0 || index >= NUM_HUFF_TBLS)
           return -1;

       if (*hufftbl == NULL)
           *hufftbl = jpeg_alloc_huff_table ((j_common_ptr)info);
       if (*hufftbl == NULL)
           return -1;

       memcpy ((*hufftbl)->bits, bits, sizeof (*hufftbl)->bits);
       memcpy ((*hufftbl)->huffval, huffval, sizeof (*hufftbl)->huffval);
    }

    if (length != 0)
       return -1;

    return 0;
}

void processimage (const void *p, int l)
{

	struct jpeg_decompress_struct mycinfo;
	struct my_error_mgr myjerr;
	JSAMPARRAY jpegbuffer=NULL;
	int row_stride;

	mycinfo.err = jpeg_std_error(&myjerr.pub);
	myjerr.pub.error_exit = my_error_exit;
	if (setjmp(myjerr.setjmp_buffer)) {
		jpeg_destroy_decompress(&mycinfo);
		return ;/*exit(0);*/
	}
	jpeg_create_decompress(&mycinfo);

	jpeg_memory_src(&mycinfo, p, l) ;

	((memory_source_mgr *)mycinfo.src)->pub.next_input_byte = (JOCTET*)p;
	((memory_source_mgr *)mycinfo.src)->pub.bytes_in_buffer = l;

	jpeg_read_header(&mycinfo, TRUE);

	mycinfo.out_color_space = JCS_RGB;
	mycinfo.dct_method = JDCT_IFAST;
	mycinfo.jpeg_color_space = JCS_YCbCr;

	my_jpeg_load_dht( &mycinfo, 
		my_jpeg_odml_dht, 
		mycinfo.ac_huff_tbl_ptrs, 
		mycinfo.dc_huff_tbl_ptrs ); 

	jpeg_start_decompress(&mycinfo);

	row_stride = mycinfo.image_width * mycinfo.num_components;

	if(rgb == NULL){
		IMG_WIDTH=mycinfo.image_width;
		IMG_HEIGHT=mycinfo.image_height;
		rgb = (int *)malloc(sizeof(int) * (IMG_WIDTH*IMG_HEIGHT));
	}

	if(jpegbuffer==NULL){
		jpegbuffer = (*mycinfo.mem->alloc_sarray)
			((j_common_ptr) &mycinfo, JPOOL_IMAGE, row_stride, 1);
	}

	int y = 0;
	int *outp=rgb;
	while ( mycinfo.output_scanline < mycinfo.image_height) {

		jpeg_read_scanlines(&mycinfo, jpegbuffer, 1);

		int xx;
		int x3;

		for(xx = 0, x3 = 0; xx < IMG_WIDTH && x3 < row_stride; xx++, x3 += 3)
		{
			outp[y+xx] = 0xff000000 | jpegbuffer[0][x3 + 2]<<16
				| jpegbuffer[0][x3 + 1]<<8 | jpegbuffer[0][x3 + 0];
		}

		y+=IMG_WIDTH;
	}

	jpeg_finish_decompress(&mycinfo);
	jpeg_destroy_decompress(&mycinfo);
	
}

int Java_com_camera_simplemjpeg_MjpegInputStream_pixeltobmp( JNIEnv* env,jobject thiz,
	 jbyteArray jp, jint l, jobject bmp){



	jboolean b;
		
		jbyte *p=(*env)->GetByteArrayElements(env,jp,&b);

	processimage ((const void *)p, l);

		AndroidBitmapInfo  info;
		void*              pixels;
		int                ret;
		int i;
		int *colors;

		int width = IMG_WIDTH;
		int height = IMG_HEIGHT;
		

			if(bmp==NULL) return;
			if ((ret = AndroidBitmap_getInfo(env, bmp, &info)) < 0) {
				LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
				(*env)->ReleaseByteArrayElements(env, jp, p, 0);
				return -1;
			}

			if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
				LOGE("Bitmap format is not RGBA_8888 !");
				(*env)->ReleaseByteArrayElements(env, jp, p, 0);
				return -1;
			}
			if (info.width != IMG_WIDTH || info.height != IMG_HEIGHT){
				LOGE("Bitmap size differs !");
				(*env)->ReleaseByteArrayElements(env, jp, p, 0);
				return -1;
			}
			

			if ((ret = AndroidBitmap_lockPixels(env, bmp, &pixels)) < 0) {
				LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
			}


		colors = (int*)pixels;
		int *lrgb = &rgb[0];

		for(i=0 ; i<width*height ; i++){
			*colors++ = *lrgb++;
		}

			AndroidBitmap_unlockPixels(env, bmp);

		(*env)->ReleaseByteArrayElements(env, jp, p, 0);
		
		return 0;
}

void Java_com_camera_simplemjpeg_MjpegInputStream_freeCameraMemory( JNIEnv* env,jobject thiz){

	if(rgb) free(rgb);
	rgb = NULL;

}
